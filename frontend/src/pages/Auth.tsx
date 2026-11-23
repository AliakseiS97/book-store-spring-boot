import { useEffect, useState, type FormEvent } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { api } from '../api';
import { Field, Status, errorText } from '../components';
import { useStore } from '../store';

function useReturnPath() {
  const location = useLocation();
  return (location.state as { from?: string } | null)?.from ?? '/';
}

export function Login() {
  const { signIn, signedIn } = useStore();
  const navigate = useNavigate();
  const from = useReturnPath();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (signedIn) navigate(from, { replace: true });
  }, [signedIn, from, navigate]);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setBusy(true);
    setError(null);
    try {
      await signIn(email.trim(), password);
    } catch (reason) {
      setError(errorText(reason));
    } finally {
      setBusy(false);
    }
  }

  return (
    <section className="form-page">
      <h1>Welcome back</h1>
      <form onSubmit={submit}>
        {error && <Status tone="error">{error}</Status>}
        <Field label="Email" type="email" autoComplete="email" required value={email}
          onChange={(event) => setEmail(event.target.value)} />
        <Field label="Password" type="password" autoComplete="current-password" required value={password}
          onChange={(event) => setPassword(event.target.value)} />
        <button className="button wide" disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button>
      </form>
      <p className="muted">New here? <Link to="/register" state={{ from }}>Create an account</Link></p>
    </section>
  );
}

export function Register() {
  const { signIn, signedIn } = useStore();
  const navigate = useNavigate();
  const from = useReturnPath();
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    shippingAddress: '',
    password: '',
    repeatPassword: '',
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (signedIn) navigate(from, { replace: true });
  }, [signedIn, from, navigate]);

  const bind = (key: keyof typeof form) => ({
    value: form[key],
    required: true,
    onChange: (event: React.ChangeEvent<HTMLInputElement>) =>
      setForm((current) => ({ ...current, [key]: event.target.value })),
  });

  async function submit(event: FormEvent) {
    event.preventDefault();
    if (form.password !== form.repeatPassword) {
      setError('The passwords do not match.');
      return;
    }
    setBusy(true);
    setError(null);
    try {
      await api.register({ ...form, email: form.email.trim() });
      await signIn(form.email.trim(), form.password);
    } catch (reason) {
      setError(errorText(reason));
    } finally {
      setBusy(false);
    }
  }

  return (
    <section className="form-page">
      <h1>Create an account</h1>
      <form onSubmit={submit}>
        {error && <Status tone="error">{error}</Status>}
        <div className="two">
          <Field label="First name" autoComplete="given-name" {...bind('firstName')} />
          <Field label="Last name" autoComplete="family-name" {...bind('lastName')} />
        </div>
        <Field label="Email" type="email" autoComplete="email" {...bind('email')} />
        <Field label="Shipping address" autoComplete="street-address" {...bind('shippingAddress')} />
        <Field label="Password" type="password" autoComplete="new-password" {...bind('password')} />
        <Field label="Repeat password" type="password" autoComplete="new-password" {...bind('repeatPassword')} />
        <button className="button wide" disabled={busy}>{busy ? 'Creating…' : 'Create account'}</button>
      </form>
      <p className="muted">Already registered? <Link to="/login" state={{ from }}>Sign in</Link></p>
    </section>
  );
}
