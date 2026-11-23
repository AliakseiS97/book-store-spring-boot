import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Link, Route, Routes } from 'react-router-dom';
import { Layout } from './components';
import { Login, Register } from './pages/Auth';
import { BookPage } from './pages/BookPage';
import { CartPage } from './pages/CartPage';
import { Catalog } from './pages/Catalog';
import { Orders } from './pages/Orders';
import { StoreProvider } from './store';
import './styles.css';

function NotFound() {
  return (
    <section className="narrow center">
      <h1>This page is out of print</h1>
      <Link className="button" to="/">Back to the shelf</Link>
    </section>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <StoreProvider>
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<Catalog />} />
            <Route path="books/:id" element={<BookPage />} />
            <Route path="login" element={<Login />} />
            <Route path="register" element={<Register />} />
            <Route path="cart" element={<CartPage />} />
            <Route path="orders" element={<Orders />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </StoreProvider>
    </BrowserRouter>
  </StrictMode>,
);
