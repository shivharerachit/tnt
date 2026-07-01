// Login screen.
import { useState } from "react";
import { Link, useNavigate } from "../lib/router";
import { useAuth } from "../hooks/useAuth";
import { validateEmail, validatePassword } from "../utils/validation";
import { APP_CONFIG } from "../config/app-config";
import TextField from "../components/TextField";
import Button from "../components/Button";

export default function LoginPage() {
  const { signIn } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({ email: "", password: "" });
  const [touched, setTouched] = useState({ email: false, password: false });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validateField(field, value) {
    if (field === "email") {
      return validateEmail(value);
    }

    if (field === "password") {
      return validatePassword(value);
    }

    return "";
  }

  function updateField(field, value) {
    if (field === "email") {
      setEmail(value);
    }

    if (field === "password") {
      setPassword(value);
    }

    if (touched[field]) {
      setErrors((prev) => ({ ...prev, [field]: validateField(field, value) }));
    }

    setError("");
  }

  function handleBlur(field, value) {
    setTouched((prev) => ({ ...prev, [field]: true }));
    setErrors((prev) => ({ ...prev, [field]: validateField(field, value) }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    const nextErrors = {
      email: validateEmail(email),
      password: validatePassword(password),
    };

    setTouched({ email: true, password: true });
    setErrors(nextErrors);

    if (Object.values(nextErrors).some(Boolean)) {
      return;
    }

    setIsSubmitting(true);
    try {
      await signIn({ email, password });
      navigate({ to: "/dashboard" });
    } catch (err) {
      setError(err.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="center-screen">
      <div className="card auth-card">
        <div className="auth-logo">
          <h1 className="page-title">{APP_CONFIG.APP_NAME}</h1>
          <p className="text-muted">Sign in to continue</p>
        </div>

        {error && <p className="alert alert-error">{error}</p>}

        <form onSubmit={handleSubmit} noValidate>
          <TextField
            label="Email"
            type="email"
            value={email}
            onChange={(value) => updateField("email", value)}
            onBlur={() => handleBlur("email", email)}
            error={touched.email ? errors.email : ""}
            placeholder="you@example.com"
            autoComplete="email"
          />
          <TextField
            label="Password"
            type="password"
            value={password}
            onChange={(value) => updateField("password", value)}
            onBlur={() => handleBlur("password", password)}
            error={touched.password ? errors.password : ""}
            placeholder="Your password"
            autoComplete="current-password"
          />
          <Button type="submit" block disabled={isSubmitting}>
            {isSubmitting ? "Signing in..." : "Sign in"}
          </Button>
        </form>

        <p className="auth-hint">
          Don&apos;t have an account? <Link to="/register">Create one</Link>
        </p>
      </div>
    </div>
  );
}
