export interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  price: number;
  description?: string | null;
  coverImage?: string | null;
  categoryIds?: number[];
}

export interface Category {
  id: number;
  name: string;
  description?: string | null;
}

export interface CartItem {
  id: number;
  bookId: number;
  bookTitle: string;
  quantity: number;
}

export interface Cart {
  id: number;
  userId: number;
  cartItems: CartItem[];
}

export interface OrderItem {
  id: number;
  bookId: number;
  quantity: number;
}

export interface Order {
  id: number;
  userId: number;
  orderItems: OrderItem[];
  orderDate: string;
  total: number;
  status: 'PENDING' | 'COMPLETED' | 'DELIVERED';
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  number: number;
}

export interface Registration {
  email: string;
  password: string;
  repeatPassword: string;
  firstName: string;
  lastName: string;
  shippingAddress: string;
}

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
  ) {
    super(message);
  }
}

const TOKEN_KEY = 'bookstore.token';

export function readToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

export function writeToken(token: string | null) {
  try {
    if (token) localStorage.setItem(TOKEN_KEY, token);
    else localStorage.removeItem(TOKEN_KEY);
  } catch {
    // Storage can be blocked. The session then lasts until the page reloads.
  }
}

let onUnauthorized: () => void = () => {};
export function setUnauthorizedHandler(handler: () => void) {
  onUnauthorized = handler;
}

function messageFrom(data: unknown, status: number): string {
  if (data && typeof data === 'object') {
    const { errors, message } = data as { errors?: unknown; message?: unknown };
    if (Array.isArray(errors) && errors.length) return errors.map(String).join('. ');
    if (typeof errors === 'string') return errors;
    if (typeof message === 'string' && message) return message;
  }
  if (status === 401) return 'Your email or password is incorrect.';
  if (status === 403) return 'This account cannot do that.';
  if (status >= 500) return 'The shop is having trouble. Please try again in a moment.';
  return 'Something went wrong. Please try again.';
}

async function request<T>(path: string, method = 'GET', body?: unknown): Promise<T> {
  const token = readToken();
  const headers: Record<string, string> = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (token) headers.Authorization = `Bearer ${token}`;

  let response: Response;
  try {
    response = await fetch(`/api${path}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    });
  } catch {
    throw new ApiError('Cannot reach the shop. Check your connection.', 0);
  }

  if (response.status === 204) return undefined as T;
  const text = await response.text();
  let data: unknown = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = null;
  }

  if (!response.ok) {
    if (response.status === 401 && token) onUnauthorized();
    throw new ApiError(messageFrom(data, response.status), response.status);
  }
  return data as T;
}

const query = (params: Record<string, string | number>) =>
  new URLSearchParams(Object.entries(params).map(([k, v]) => [k, String(v)])).toString();

export const api = {
  login: (email: string, password: string) =>
    request<{ token: string }>('/auth/login', 'POST', { email, password }),
  register: (form: Registration) => request<unknown>('/auth/registration', 'POST', form),

  categories: () => request<Category[]>(`/categories?${query({ size: 50, sort: 'name,asc' })}`),
  book: (id: number) => request<Book>(`/books/${id}`),
  books: (page: number, size: number, sort: string) =>
    request<Page<Book>>(`/books?${query({ page, size, sort })}`),
  booksInCategory: (id: number, page: number, size: number, sort: string) =>
    request<Book[]>(`/categories/${id}/books?${query({ page, size, sort })}`),

  /** The API filters title and author together, so a single search box asks for each one. */
  async search(text: string): Promise<Book[]> {
    const [byTitle, byAuthor] = await Promise.all(
      ['titles', 'authors'].map((field) =>
        request<Page<Book>>(`/books/search?${query({ [field]: text, size: 100 })}`),
      ),
    );
    const merged = new Map<number, Book>();
    [...byTitle.content, ...byAuthor.content].forEach((book) => merged.set(book.id, book));
    return [...merged.values()];
  },

  cart: () => request<Cart>('/cart'),
  addToCart: (bookId: number, quantity: number) =>
    request<Cart>('/cart', 'POST', { bookId, quantity }),
  updateCartItem: (id: number, quantity: number) =>
    request<Cart>(`/cart/cart-items/${id}`, 'PUT', { quantity }),
  removeCartItem: (id: number) => request<void>(`/cart/cart-items/${id}`, 'DELETE'),

  orders: () => request<Page<Order>>(`/orders?${query({ size: 50, sort: 'orderDate,desc' })}`),
  placeOrder: (shippingAddress: string) =>
    request<Order>('/orders', 'POST', { shippingAddress }),
};
