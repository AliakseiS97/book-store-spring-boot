import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api, type Book, type Order } from '../api';
import { RequireSignIn, Status, errorText, formatPrice } from '../components';

const dateFormat = new Intl.DateTimeFormat('en-GB', { dateStyle: 'medium', timeStyle: 'short' });

function OrdersView() {
  const [orders, setOrders] = useState<Order[] | null>(null);
  const [books, setBooks] = useState<Record<number, Book>>({});
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let current = true;
    api.orders()
      .then(async ({ content }) => {
        if (!current) return;
        setOrders(content);
        const ids = [...new Set(content.flatMap((order) => order.orderItems.map((item) => item.bookId)))];
        const list = await Promise.all(ids.map((id) => api.book(id).catch(() => null)));
        if (!current) return;
        const next: Record<number, Book> = {};
        list.forEach((book) => {
          if (book) next[book.id] = book;
        });
        setBooks(next);
      })
      .catch((reason) => current && setError(errorText(reason)));
    return () => {
      current = false;
    };
  }, []);

  if (error) return <Status tone="error">{error}</Status>;
  if (!orders) return <Status>Loading your orders…</Status>;
  if (orders.length === 0) {
    return (
      <section className="narrow center">
        <h1>No orders yet</h1>
        <p className="muted">When you place an order, it will appear here.</p>
        <Link className="button" to="/">Browse books</Link>
      </section>
    );
  }

  return (
    <section className="orders">
      <h1 className="page-title">Your orders</h1>
      {orders.map((order) => (
        <article key={order.id} className="order">
          <header>
            <div>
              <strong>Order #{order.id}</strong>
              <span className="muted">{dateFormat.format(new Date(order.orderDate))}</span>
            </div>
            <span className={`badge badge-${order.status.toLowerCase()}`}>{order.status.toLowerCase()}</span>
          </header>
          <ul>
            {order.orderItems.map((item) => (
              <li key={item.id}>
                <Link to={`/books/${item.bookId}`}>{books[item.bookId]?.title ?? `Book #${item.bookId}`}</Link>
                <span className="muted">× {item.quantity}</span>
              </li>
            ))}
          </ul>
          <footer><span>Total</span><strong>{formatPrice(order.total)}</strong></footer>
        </article>
      ))}
    </section>
  );
}

export function Orders() {
  return <RequireSignIn><OrdersView /></RequireSignIn>;
}
