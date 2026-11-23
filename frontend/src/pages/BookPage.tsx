import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api, type Book, type Category } from '../api';
import { AddToCart, Cover, Status, errorText, formatPrice } from '../components';

export function BookPage() {
  const id = Number(useParams().id);
  const [book, setBook] = useState<Book | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [quantity, setQuantity] = useState(1);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let current = true;
    setBook(null);
    setError(null);
    setQuantity(1);
    api.book(id)
      .then((result) => current && setBook(result))
      .catch((reason) => current && setError(errorText(reason)));
    api.categories().then((list) => current && setCategories(list)).catch(() => {});
    return () => {
      current = false;
    };
  }, [id]);

  if (error) {
    return (
      <section className="narrow">
        <Status tone="error">{error}</Status>
        <Link to="/">Back to the shelf</Link>
      </section>
    );
  }
  if (!book) return <Status>Finding your book…</Status>;

  const tags = categories.filter((category) => book.categoryIds?.includes(category.id));

  return (
    <article className="book-page">
      <Cover book={book} large />
      <div>
        <Link to="/" className="back">← All books</Link>
        <div className="tags">
          {tags.map((tag) => (
            <Link key={tag.id} to={`/?category=${tag.id}`}>{tag.name}</Link>
          ))}
        </div>
        <h1>{book.title}</h1>
        <p className="byline">by {book.author}</p>
        <p className="big-price">{formatPrice(book.price)}</p>
        {book.description && <p className="lede">{book.description}</p>}
        <div className="buy-row">
          <label className="qty">
            <span className="muted">Qty</span>
            <select value={quantity} onChange={(event) => setQuantity(Number(event.target.value))}>
              {[1, 2, 3, 4, 5].map((value) => <option key={value}>{value}</option>)}
            </select>
          </label>
          <AddToCart bookId={book.id} quantity={quantity} />
        </div>
        <dl className="details">
          <div><dt>ISBN</dt><dd>{book.isbn}</dd></div>
        </dl>
      </div>
    </article>
  );
}
