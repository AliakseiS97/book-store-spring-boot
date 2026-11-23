import { useEffect, useMemo, useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api, type Book, type CartItem } from '../api';
import { Cover, Field, RequireSignIn, Status, errorText, formatPrice } from '../components';
import { useStore } from '../store';

function CartView() {
  const { cart, refreshCart, setCart, notify } = useStore();
  const navigate = useNavigate();
  const [books, setBooks] = useState<Record<number, Book>>({});
  const [loaded, setLoaded] = useState(false);
  const [busyItem, setBusyItem] = useState<number | null>(null);
  const [address, setAddress] = useState('');
  const [placing, setPlacing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    refreshCart().finally(() => setLoaded(true));
  }, [refreshCart]);

  const items = useMemo(
    () => [...(cart?.cartItems ?? [])].sort((a, b) => a.id - b.id),
    [cart],
  );
  const bookIds = items.map((item) => item.bookId).join(',');

  useEffect(() => {
    if (!bookIds) return;
    let current = true;
    Promise.all(bookIds.split(',').map((id) => api.book(Number(id)).catch(() => null))).then((list) => {
      if (!current) return;
      const next: Record<number, Book> = {};
      list.forEach((book) => {
        if (book) next[book.id] = book;
      });
      setBooks(next);
    });
    return () => {
      current = false;
    };
  }, [bookIds]);

  const pricesReady = items.every((item) => books[item.bookId]);
  const total = items.reduce((sum, item) => sum + (books[item.bookId]?.price ?? 0) * item.quantity, 0);

  async function changeQuantity(item: CartItem, quantity: number) {
    setBusyItem(item.id);
    try {
      setCart(await api.updateCartItem(item.id, quantity));
    } catch (reason) {
      notify(errorText(reason));
    } finally {
      setBusyItem(null);
    }
  }

  async function remove(item: CartItem) {
    setBusyItem(item.id);
    try {
      await api.removeCartItem(item.id);
      await refreshCart();
    } catch (reason) {
      notify(errorText(reason));
    } finally {
      setBusyItem(null);
    }
  }

  async function checkout(event: FormEvent) {
    event.preventDefault();
    setPlacing(true);
    setError(null);
    try {
      await api.placeOrder(address.trim());
      await refreshCart();
      notify('Order placed');
      navigate('/orders');
    } catch (reason) {
      setError(errorText(reason));
    } finally {
      setPlacing(false);
    }
  }

  if (!loaded) return <Status>Opening your bag…</Status>;
  if (!cart) return <Status tone="error">We could not open your bag. This account may not support shopping.</Status>;
  if (items.length === 0) {
    return (
      <section className="narrow center">
        <h1>Your bag is empty</h1>
        <p className="muted">There is plenty on the shelf.</p>
        <Link className="button" to="/">Browse books</Link>
      </section>
    );
  }

  return (
    <section>
      <h1 className="page-title">Your bag</h1>
      <div className="cart-layout">
        <ul className="cart-list">
          {items.map((item) => {
            const book = books[item.bookId];
            return (
              <li key={item.id}>
                <Link to={`/books/${item.bookId}`} tabIndex={-1}>
                  <Cover book={book ?? { title: item.bookTitle, author: '' }} />
                </Link>
                <div>
                  <Link to={`/books/${item.bookId}`}><strong>{item.bookTitle}</strong></Link>
                  <p className="muted">{book?.author}</p>
                  <button className="link" disabled={busyItem === item.id} onClick={() => remove(item)}>
                    Remove
                  </button>
                </div>
                <div className="stepper" role="group" aria-label={`Quantity of ${item.bookTitle}`}>
                  <button
                    aria-label="Decrease quantity"
                    disabled={busyItem === item.id || item.quantity <= 1}
                    onClick={() => changeQuantity(item, item.quantity - 1)}
                  >−</button>
                  <span>{item.quantity}</span>
                  <button
                    aria-label="Increase quantity"
                    disabled={busyItem === item.id}
                    onClick={() => changeQuantity(item, item.quantity + 1)}
                  >+</button>
                </div>
                <strong className="line-price">
                  {book ? formatPrice(book.price * item.quantity) : '…'}
                </strong>
              </li>
            );
          })}
        </ul>

        <form className="summary" onSubmit={checkout}>
          <h2>Checkout</h2>
          {error && <Status tone="error">{error}</Status>}
          <Field label="Shipping address" autoComplete="street-address" required value={address}
            onChange={(event) => setAddress(event.target.value)} />
          <div className="total">
            <span>Total</span>
            <strong>{pricesReady ? formatPrice(total) : '…'}</strong>
          </div>
          <button className="button wide" disabled={placing || !pricesReady}>
            {placing ? 'Placing order…' : 'Place order'}
          </button>
          <small className="muted">Demo shop. No payment is taken.</small>
        </form>
      </div>
    </section>
  );
}

export function CartPage() {
  return <RequireSignIn><CartView /></RequireSignIn>;
}
