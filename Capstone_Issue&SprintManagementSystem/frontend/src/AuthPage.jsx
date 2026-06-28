import { useState } from "react";
import { registerUser } from "./auth";

const initialForm = { name: "", email: "", password: "", role: "member" };

function AuthPage() {
  const [form, setForm] = useState(initialForm);
  const [isRegister, setIsRegister] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    const payload = isRegister
      ? { name: form.name, email: form.email, password: form.password, role: form.role }
      : { email: form.email, password: form.password };

    const result = isRegister ? await registerUser(payload) : await loginUser(payload);

    if (result.detail) {
      setError(result.detail);
      return;
    }

    if (result.access_token) {
      setMessage("Login successful. You can continue to the dashboard.");
      return;
    }

    setMessage("Registration successful. You can now log in.");
    if (isRegister) setIsRegister(false);
  };

  return (
    <div className="container">
      <h1>{isRegister ? "Register" : "Login"}</h1>
      {message && <p className={`message ${error ? "error" : ""}`}>{message || error}</p>}
      <form onSubmit={handleSubmit}>
        {isRegister && (
          <>
            <label htmlFor="name">Name</label>
            <input id="name" name="name" value={form.name} onChange={handleChange} placeholder="John Doe" />
          </>
        )}
        <label htmlFor="email">Email</label>
        <input id="email" name="email" type="email" value={form.email} onChange={handleChange} placeholder="you@example.com" />

        <label htmlFor="password">Password</label>
        <input id="password" name="password" type="password" value={form.password} onChange={handleChange} placeholder="Enter password" />

        <button type="submit">{isRegister ? "Create account" : "Sign in"}</button>
      </form>
      <div className="switch-action">
        {isRegister ? "Already have an account?" : "Need an account?"}
        <button type="button" onClick={() => setIsRegister(!isRegister)}>
          {isRegister ? "Login" : "Register"}
        </button>
      </div>
    </div>
  );
}

export default AuthPage;
