import { useEffect, useState, type FormEvent, type ReactNode } from 'react';
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { ApiError, api, type Book } from './api';
import { useStore } from './store';
import { ThemeToggle } from './theme';

const money = new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' });
export const formatPrice = (value: number) => money.format(value);

export const errorText = (error: unknown) =>
  error instanceof ApiError ? error.message : 'Something went wrong. Please try again.';

export const BINDINGS = [
  ['#7c2f2a', '#f5dfc0'],
  ['#1f4f40', '#f1dfa9'],
  ['#25365a', '#efd9c3'],
  ['#b8842f', '#22170b'],
  ['#54344f', '#f3dccb'],
  ['#d8c8a4', '#33230f'],
  ['#35524a', '#f4e6c8'],
  ['#964a2e', '#fbeed7'],
];

export function hash(text: string) {
  let value = 0;
  for (const char of text) value = (value * 31 + char.charCodeAt(0)) >>> 0;
  return value;
}

export function Feather({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 64 64" aria-hidden="true">
      <path
        fill="currentColor"
        d="M55 6C39 6.700 27 13.800 21.700 26.700c-2.200 5.500-2.700 11-1.800 16L10 56.500l3.200 2.200 9.800-13.600c4.900.700 10.400-.400 15.500-3.100l-6-1.800 10.700-3.100c2.200-1.800 4.200-4 5.800-6.500l-6.900-1.100 9.800-3.800C54.100 19.700 55.200 13.100 55 6z"
      />
      <path fill="none" stroke="var(--feather-vein)" strokeWidth="1.4" d="M47 14C36 22 27 33 16 50" />
    </svg>
  );
}

export function Cover({
  book,
  large = false,
}: {
  book: Pick<Book, 'title' | 'author' | 'coverImage'>;
  large?: boolean;
}) {
  const url = book.coverImage && !book.coverImage.includes('example.com') ? book.coverImage : null;
  const [loaded, setLoaded] = useState<string | null>(null);

  useEffect(() => {
    if (!url) return;
    let current = true;
    const image = new Image();
    image.onload = () => {
      if (current && image.naturalWidth > 1) setLoaded(url);
    };
    image.src = url;
    return () => {
      current = false;
    };
  }, [url]);

  if (url && loaded === url) {
    return (
      <div className={`cover cover-photo${large ? ' cover-large' : ''}`} aria-hidden="true">
        <img src={url} alt="" />
      </div>
    );
  }

  const seed = hash(book.title);
  const [background, ink] = BINDINGS[seed % BINDINGS.length];
  return (
    <div className={`cover${large ? ' cover-large' : ''}`} style={{ background, color: ink }} aria-hidden="true">
      <span className="cover-title">{book.title}</span>
      <span className="cover-author">{book.author}</span>
    </div>
  );
}

export function BookCard({ book }: { book: Book }) {
  return (
    <article className="book-card">
      <Link to={`/books/${book.id}`}>
        <div className="stand"><Cover book={book} /></div>
        <h3>{book.title}</h3>
        <p className="muted">{book.author}</p>
        <p className="price">{formatPrice(book.price)}</p>
      </Link>
    </article>
  );
}

export function AddToCart({
  bookId,
  quantity = 1,
  className = 'button',
}: {
  bookId: number;
  quantity?: number;
  className?: string;
}) {
  const { signedIn, setCart, notify } = useStore();
  const [busy, setBusy] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  async function add() {
    if (!signedIn) {
      navigate('/login', { state: { from: location.pathname } });
      return;
    }
    setBusy(true);
    try {
      setCart(await api.addToCart(bookId, quantity));
      notify('Added to your bag');
    } catch (error) {
      notify(errorText(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <button className={className} onClick={add} disabled={busy}>
      {busy ? 'Adding…' : 'Add to bag'}
    </button>
  );
}

export function Field({
  label,
  hint,
  ...input
}: { label: string; hint?: string } & React.InputHTMLAttributes<HTMLInputElement>) {
  return (
    <label className="field">
      <span>{label}</span>
      <input {...input} />
      {hint && <small className="muted">{hint}</small>}
    </label>
  );
}

export function Status({ children, tone = 'plain' }: { children: ReactNode; tone?: 'plain' | 'error' }) {
  return (
    <div className={`status status-${tone}`} role={tone === 'error' ? 'alert' : 'status'}>
      {children}
    </div>
  );
}

export function RequireSignIn({ children }: { children: ReactNode }) {
  const { signedIn } = useStore();
  const location = useLocation();
  if (signedIn) return <>{children}</>;
  return (
    <section className="narrow center">
      <h1>Sign in to continue</h1>
      <p className="muted">Your bag and orders are saved to your account.</p>
      <Link className="button" to="/login" state={{ from: location.pathname }}>
        Sign in
      </Link>
    </section>
  );
}

function Search() {
  const navigate = useNavigate();
  const [text, setText] = useState(() => new URLSearchParams(window.location.search).get('q') ?? '');

  function submit(event: FormEvent) {
    event.preventDefault();
    const q = text.trim();
    navigate(q ? `/?q=${encodeURIComponent(q)}` : '/');
  }

  return (
    <form className="search" role="search" onSubmit={submit}>
      <input
        type="search"
        aria-label="Search by title or author"
        placeholder="Search title or author"
        value={text}
        onChange={(event) => setText(event.target.value)}
      />
      <button type="submit">Search</button>
    </form>
  );
}

export function Layout() {
  const { signedIn, signOut, cartCount, notice } = useStore();
  const navigate = useNavigate();

  return (
    <>
      <a className="skip" href="#main">Skip to content</a>
      <header className="site-header">
        <div className="wrap header-row">
          <Link to="/" className="logo">
            <Feather className="logo-feather" />
            Book Store
          </Link>
          <Search />
          <nav aria-label="Account">
            {signedIn ? (
              <>
                <NavLink to="/orders">Orders</NavLink>
                <button
                  className="link"
                  onClick={() => {
                    signOut();
                    navigate('/');
                  }}
                >
                  Sign out
                </button>
              </>
            ) : (
              <NavLink to="/login">Sign in</NavLink>
            )}
            <NavLink to="/cart" className="bag" aria-label={`Bag, ${cartCount} items`}>
              Bag <b>{cartCount}</b>
            </NavLink>
            <ThemeToggle />
          </nav>
        </div>
      </header>

      <main id="main" className="wrap">
        <Outlet />
      </main>

      <footer className="site-footer">
        <div className="wrap footer-row">
          <span className="logo"><Feather className="logo-feather" />Book Store</span>
          <span className="muted">Demo shop for a Spring Boot REST API. Orders are not real.</span>
        </div>
      </footer>

      <div className="toast-slot" aria-live="polite">
        {notice && <div className="toast">{notice}</div>}
      </div>
    </>
  );
}
