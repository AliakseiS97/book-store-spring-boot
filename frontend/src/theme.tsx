import { useEffect, useState } from 'react';

type Theme = 'light' | 'dark';
const KEY = 'bookstore.theme';

const currentTheme = (): Theme =>
  document.documentElement.dataset.theme === 'dark' ? 'dark' : 'light';

export function ThemeToggle() {
  const [theme, setTheme] = useState<Theme>(currentTheme);
  const next: Theme = theme === 'dark' ? 'light' : 'dark';

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    document
      .querySelector('meta[name="theme-color"]')
      ?.setAttribute('content', theme === 'dark' ? '#16231e' : '#e9ecdf');
  }, [theme]);

  function toggle() {
    setTheme(next);
    try {
      localStorage.setItem(KEY, next);
    } catch {
      // The theme still applies until the page reloads.
    }
  }

  return (
    <button className="theme-toggle" onClick={toggle} aria-label={`Switch to ${next} theme`} title={`Switch to ${next} theme`}>
      <svg viewBox="0 0 24 24" width="20" height="20" aria-hidden="true">
        {theme === 'dark' ? (
          <path
            fill="currentColor"
            d="M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10zm0-5 1 3h-2l1-3zm0 20-1-3h2l-1 3zM2 12l3-1v2l-3-1zm20 0-3 1v-2l3 1zM4.9 4.9l2.8 1.4-1.4 1.4-1.4-2.8zm14.2 14.2-2.8-1.4 1.4-1.4 1.4 2.8zM4.9 19.1l1.4-2.8 1.4 1.4-2.8 1.4zM19.1 4.9l-1.4 2.8-1.4-1.4 2.8-1.4z"
          />
        ) : (
          <path fill="currentColor" d="M20.5 14.6A8.6 8.6 0 0 1 9.4 3.5a8.9 8.9 0 1 0 11.1 11.1z" />
        )}
      </svg>
    </button>
  );
}
