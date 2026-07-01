// Registration screen.
import { useState } from "react";
import { Link, useNavigate } from "../lib/router";
import { useAuth } from "../hooks/useAuth";
import { validateEmail, validateName, validatePassword, validateRole } from "../utils/validation";
import { USER_ROLE } from "../constants";
import { APP_CONFIG } from "../config/app-config";
import TextField from "../components/TextField";
import SelectField from "../components/SelectField";
import Button from "../components/Button";

const ROLE_OPTIONS = [
  { value: USER_ROLE.MEMBER, label: "Member" },
  { value: USER_ROLE.ADMIN, label: "Admin" },
  { value: USER_ROLE.VIEWER, label: "Viewer" },
];

export default function RegisterPage() {
  const { signUp } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    role: "",
  });
  const [errors, setErrors] = useState({
    name: "",
    email: "",
    password: "",
    role: "",
  });
  const [touched, setTouched] = useState({
    name: false,
    email: false,
    password: false,
    role: false,
  });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  function validateField(field, value) {
    if (field === "name") {
      return validateName(value);
    }

    if (field === "email") {
      return validateEmail(value);
    }

    if (field === "password") {
      return validatePassword(value);
    }

    if (field === "role") {
      return validateRole(value);
    }

    return "";
  }

  function updateField(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }));

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
      name: validateName(form.name),
      email: validateEmail(form.email),
      password: validatePassword(form.password),
      role: validateRole(form.role),
    };

    setTouched({
      name: true,
      email: true,
      password: true,
      role: true,
    });
    setErrors(nextErrors);

    if (Object.values(nextErrors).some(Boolean)) {
      return;
    }

    setIsSubmitting(true);
    try {
      await signUp(form);
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
          <h1 className="page-title">Create your account</h1>
          <p className="text-muted">{APP_CONFIG.APP_NAME}</p>
        </div>

        {error && <p className="alert alert-error">{error}</p>}

        <form onSubmit={handleSubmit} noValidate>
          <TextField
            label="Full name"
            value={form.name}
            onChange={(value) => updateField("name", value)}
            onBlur={() => handleBlur("name", form.name)}
            error={touched.name ? errors.name : ""}
            placeholder="Jane Doe"
          />
          <TextField
            label="Email"
            type="email"
            value={form.email}
            onChange={(value) => updateField("email", value)}
            onBlur={() => handleBlur("email", form.email)}
            error={touched.email ? errors.email : ""}
            placeholder="you@example.com"
          />
          <TextField
            label="Password"
            type="password"
            value={form.password}
            onChange={(value) => updateField("password", value)}
            onBlur={() => handleBlur("password", form.password)}
            error={touched.password ? errors.password : ""}
            placeholder="Create a strong password"
          />
          <SelectField
            label="Role"
            value={form.role}
            onChange={(value) => updateField("role", value)}
            onBlur={() => handleBlur("role", form.role)}
            error={touched.role ? errors.role : ""}
            placeholder="Select a role"
            options={ROLE_OPTIONS}
          />
          <Button type="submit" block disabled={isSubmitting}>
            {isSubmitting ? "Creating account..." : "Create account"}
          </Button>
        </form>

        <p className="auth-hint">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
