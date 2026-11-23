# Book Store storefront

React and TypeScript storefront for the Book Store REST API.

## Features

- Public catalog with categories, sorting, paging and search by title or author
- Book details page
- Registration and JWT sign-in
- Shopping bag with quantity changes
- Checkout and order history
- Responsive layout, keyboard focus states and reduced-motion support

Book management for admins is available through the API and Swagger UI.

## Run with Docker

From the repository root:

```bash
docker compose up --build
```

The shop opens at `http://localhost:3000`. Set `WEB_LOCAL_PORT` in `.env` to use another port.
nginx serves the app and proxies `/api` to Spring Boot, so both share one origin and CORS stays off.

## Run in development

Start the backend on port 8080, then:

```bash
cd frontend
npm install
npm run dev
```

Vite opens on `http://localhost:5173` and proxies `/api`. Set `API_URL` to use another backend address.
