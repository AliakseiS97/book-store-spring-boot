import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { api, type Book, type Category } from '../api';
import { BookCard, Status, errorText } from '../components';

const PAGE_SIZE = 12;
const FEATURED_ISBNS = [
  '9780441172719',
  '9780132350884',
  '9780062316097',
  '9780735211292',
  '9780451524935',
];
const SORTS = [
  ['title,asc', 'Title A to Z'],
  ['price,asc', 'Price, low to high'],
  ['price,desc', 'Price, high to low'],
  ['author,asc', 'Author A to Z'],
] as const;

function sortBooks(books: Book[], sort: string) {
  const [field, direction] = sort.split(',') as ['title' | 'author' | 'price', string];
  const sign = direction === 'desc' ? -1 : 1;
  return [...books].sort((a, b) =>
    field === 'price' ? sign * (a.price - b.price) : sign * a[field].localeCompare(b[field]),
  );
}

async function loadBooks(q: string, categoryId: number | null, sort: string, page: number) {
  if (q) {
    const found = await api.search(q);
    const filtered = categoryId
      ? found.filter((book) => book.categoryIds?.includes(categoryId))
      : found;
    const sorted = sortBooks(filtered, sort);
    const start = page * PAGE_SIZE;
    return { books: sorted.slice(start, start + PAGE_SIZE), hasNext: sorted.length > start + PAGE_SIZE };
  }
  if (categoryId) {
    const list = await api.booksInCategory(categoryId, page, PAGE_SIZE + 1, sort);
    return { books: list.slice(0, PAGE_SIZE), hasNext: list.length > PAGE_SIZE };
  }
  const result = await api.books(page, PAGE_SIZE, sort);
  return { books: result.content, hasNext: result.number + 1 < result.totalPages };
}

function FeaturedBooks() {
  const [books, setBooks] = useState<Book[]>([]);

  useEffect(() => {
    let current = true;
    api.books(0, 50, 'title,asc')
      .then(({ content }) => {
        if (!current) return;
        const picked = FEATURED_ISBNS
          .map((isbn) => content.find((book) => book.isbn === isbn))
          .filter((book): book is Book => Boolean(book));
        setBooks(picked.length === FEATURED_ISBNS.length ? picked : content.slice(0, FEATURED_ISBNS.length));
      })
      .catch(() => current && setBooks([]));
    return () => {
      current = false;
    };
  }, []);

  if (books.length === 0) return null;
  return (
    <div className="featured">
      <h2>Featured books</h2>
      <div className="featured-row">
        {books.map((book) => <BookCard key={book.id} book={book} />)}
      </div>
    </div>
  );
}

export function Catalog() {
  const [params, setParams] = useSearchParams();
  const q = params.get('q')?.trim() ?? '';
  const categoryId = Number(params.get('category')) || null;
  const sort = SORTS.some(([value]) => value === params.get('sort')) ? params.get('sort')! : 'title,asc';
  const page = Math.max(0, Number(params.get('page')) || 0);

  const [categories, setCategories] = useState<Category[]>([]);
  const [books, setBooks] = useState<Book[] | null>(null);
  const [hasNext, setHasNext] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [attempt, setAttempt] = useState(0);
  const [filtersOpen, setFiltersOpen] = useState(false);

  useEffect(() => {
    api.categories().then(setCategories).catch(() => setCategories([]));
  }, []);

  useEffect(() => {
    let current = true;
    setBooks(null);
    setError(null);
    loadBooks(q, categoryId, sort, page)
      .then((result) => {
        if (!current) return;
        setBooks(result.books);
        setHasNext(result.hasNext);
      })
      .catch((reason) => current && setError(errorText(reason)));
    return () => {
      current = false;
    };
  }, [q, categoryId, sort, page, attempt]);

  function update(changes: Record<string, string | null>) {
    const next = new URLSearchParams(params);
    if (!('page' in changes)) next.delete('page');
    Object.entries(changes).forEach(([key, value]) => {
      if (value) next.set(key, value);
      else next.delete(key);
    });
    setParams(next);
  }

  const showHero = !q && !categoryId && page === 0 && sort === 'title,asc';
  const heading = q
    ? `Results for “${q}”`
    : categories.find((category) => category.id === categoryId)?.name ?? 'All books';

  return (
    <>
      {showHero && (
        <section className="hero">
          <div className="hero-text">
            <h1>Find your next book</h1>
            <p className="lede">Programming, fiction, science, history and business in one shop.</p>
            <a className="button" href="#shelf">Browse all books</a>
          </div>
          <FeaturedBooks />
        </section>
      )}

      <div id="shelf" className="catalog">
        <aside className={`filters${filtersOpen ? ' filters-open' : ''}`} aria-label="Sort and filter">
          <button
            className="filters-toggle"
            aria-expanded={filtersOpen}
            onClick={() => setFiltersOpen((open) => !open)}
          >
            Sort and filter
          </button>
          <div className="filters-body">
            <fieldset>
              <legend>Sort by</legend>
              {SORTS.map(([value, label]) => (
                <label key={value} className="radio">
                  <input
                    type="radio"
                    name="sort"
                    value={value}
                    checked={sort === value}
                    onChange={() => update({ sort: value })}
                  />
                  {label}
                </label>
              ))}
            </fieldset>
            <fieldset>
              <legend>Category</legend>
              <label className="radio">
                <input
                  type="radio"
                  name="category"
                  checked={!categoryId}
                  onChange={() => update({ category: null })}
                />
                All books
              </label>
              {categories.map((category) => (
                <label key={category.id} className="radio">
                  <input
                    type="radio"
                    name="category"
                    checked={categoryId === category.id}
                    onChange={() => update({ category: String(category.id) })}
                  />
                  {category.name}
                </label>
              ))}
            </fieldset>
          </div>
        </aside>

        <section className="shelf">
        <div className="shelf-head">
          <h2>{heading}</h2>
        </div>

        {error ? (
          <Status tone="error">
            {error}{' '}
            <button className="link" onClick={() => setAttempt((value) => value + 1)}>Try again</button>
          </Status>
        ) : !books ? (
          <div className="grid" aria-busy="true" aria-label="Loading books">
            {Array.from({ length: 8 }, (_, index) => <div className="skeleton" key={index} />)}
          </div>
        ) : books.length === 0 ? (
          <Status>
            No books match this search.{' '}
            <Link to="/">See all books</Link>
          </Status>
        ) : (
          <div className="grid">
            {books.map((book) => <BookCard key={book.id} book={book} />)}
          </div>
        )}

        {books && (page > 0 || hasNext) && (
          <nav className="pager" aria-label="Pages">
            <button
              className="button ghost"
              disabled={page === 0}
              onClick={() => update({ page: page > 1 ? String(page - 1) : null })}
            >
              Previous
            </button>
            <span className="muted">Page {page + 1}</span>
            <button
              className="button ghost"
              disabled={!hasNext}
              onClick={() => update({ page: String(page + 1) })}
            >
              Next
            </button>
          </nav>
        )}
        </section>
      </div>
    </>
  );
}
