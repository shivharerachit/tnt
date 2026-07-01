// Top navigation bar shown on authenticated pages.
import { Link, useNavigate } from "../lib/router";
// "@tanstack/react-router";
import { useAuth } from "../hooks/useAuth";
import { APP_CONFIG } from "../config/app-config";

function initials(name) {
  return String(name || "?")
    .split(" ")
    .map((part) => part[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
}

export default function Navbar() {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();

  function handleSignOut() {
    signOut();
    navigate({ to: "/login" });
  }

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/dashboard" className="navbar-brand">
          <span aria-hidden="true">🗂️</span>
          {APP_CONFIG.APP_NAME}
        </Link>

        <div className="navbar-links">
          <Link
            to="/dashboard"
            className="nav-link"
            activeProps={{ className: "nav-link active" }}
          >
            Projects
          </Link>
        </div>

        <span className="spacer" />

        {user && (
          <div className="user-chip">
            <span className="avatar" aria-hidden="true">
              {initials(user.name)}
            </span>
            <span>
              {user.name}
              <br />
              <span className="text-muted text-sm">{user.role}</span>
            </span>
            <button
              type="button"
              className="btn btn-secondary btn-sm"
              onClick={handleSignOut}
            >
              Sign out
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}
