import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from 'react';
import { api, readToken, setUnauthorizedHandler, writeToken, type Cart } from './api';

interface Store {
  signedIn: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => void;
  cart: Cart | null;
  cartCount: number;
  refreshCart: () => Promise<void>;
  setCart: (cart: Cart | null) => void;
  notice: string | null;
  notify: (message: string) => void;
}

const StoreContext = createContext<Store | null>(null);

export function StoreProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState(readToken);
  const [cart, setCart] = useState<Cart | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const timer = useRef<number>();

  const signOut = useCallback(() => {
    writeToken(null);
    setToken(null);
    setCart(null);
  }, []);

  const signIn = useCallback(async (email: string, password: string) => {
    const { token: next } = await api.login(email, password);
    writeToken(next);
    setToken(next);
  }, []);

  const refreshCart = useCallback(async () => {
    if (!readToken()) return;
    try {
      setCart(await api.cart());
    } catch {
      setCart(null);
    }
  }, []);

  const notify = useCallback((message: string) => {
    setNotice(message);
    window.clearTimeout(timer.current);
    timer.current = window.setTimeout(() => setNotice(null), 3200);
  }, []);

  useEffect(() => setUnauthorizedHandler(signOut), [signOut]);
  useEffect(() => {
    if (token) void refreshCart();
  }, [token, refreshCart]);

  const value = useMemo<Store>(
    () => ({
      signedIn: Boolean(token),
      signIn,
      signOut,
      cart,
      cartCount: cart?.cartItems.reduce((sum, item) => sum + item.quantity, 0) ?? 0,
      refreshCart,
      setCart,
      notice,
      notify,
    }),
    [token, signIn, signOut, cart, refreshCart, notice, notify],
  );

  return <StoreContext.Provider value={value}>{children}</StoreContext.Provider>;
}

export function useStore() {
  const store = useContext(StoreContext);
  if (!store) throw new Error('useStore must be used inside StoreProvider');
  return store;
}
