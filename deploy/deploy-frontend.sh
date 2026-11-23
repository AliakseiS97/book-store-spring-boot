#!/usr/bin/env bash
# Creates or updates the CloudFront stack, then publishes the frontend.
# Run from AWS CloudShell, in the repository root:  bash deploy/deploy-frontend.sh
set -euo pipefail

STACK_NAME="${STACK_NAME:-book-store-frontend}"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"

aws cloudformation deploy \
  --stack-name "$STACK_NAME" \
  --template-file "$ROOT/deploy/cloudfront.yml" \
  --no-fail-on-empty-changeset

output() {
  aws cloudformation describe-stacks \
    --stack-name "$STACK_NAME" \
    --query "Stacks[0].Outputs[?OutputKey=='$1'].OutputValue" \
    --output text
}

BUCKET="$(output BucketName)"
DISTRIBUTION="$(output DistributionId)"
SITE_URL="$(output SiteUrl)"

cd "$ROOT/frontend"
npm ci --no-audit --no-fund
npm run build

aws s3 sync dist "s3://$BUCKET" --delete --exclude index.html \
  --cache-control "public,max-age=31536000,immutable"
aws s3 cp dist/index.html "s3://$BUCKET/index.html" \
  --cache-control "no-cache" --content-type "text/html; charset=utf-8"
aws cloudfront create-invalidation --distribution-id "$DISTRIBUTION" --paths "/index.html" "/" >/dev/null

echo "Shop is published: $SITE_URL"
