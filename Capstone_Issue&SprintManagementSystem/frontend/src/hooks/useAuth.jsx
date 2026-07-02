// Authentication context + hook. Stores the current user/token and exposes
// sign-in, sign-up and sign-out helpers to the whole app.
import { createContext, useContext, useEffect, useState } from "react";
import { STORAGE_KEYS } from "../constants";
import * as authService from "../services/auth";

const AuthContext = createContext(null);

function clearBrowserSession() {
  localStorage.clear();
  sessionStorage.clear();
}

function extractToken(result) {
  return result?.token || result?.access_token || result?.accessToken || "";
}

function extractUser(result) {
  return result?.user || result?.data?.user || null;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // Load the saved session (if any) when the app starts.
  useEffect(() => {
    try {
      const savedUser = localStorage.getItem(STORAGE_KEYS.USER);
      if (savedUser) {
        setUser(JSON.parse(savedUser));
      }
    } catch {
      // ignore corrupted storage
    }
    setIsLoading(false);
  }, []);

  function saveSession(token, nextUser) {
    localStorage.setItem(STORAGE_KEYS.TOKEN, token);
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(nextUser));
    setUser(nextUser);
  }

  async function signIn(credentials) {
    const result = await authService.login(credentials);
    const token = extractToken(result);
    const nextUser = extractUser(result);

    if (!token) {
      throw new Error("Authentication token missing in login response.");
    }

    saveSession(token, nextUser);
    return nextUser;
  }

  async function signUp(data) {
    const result = await authService.register(data);
    const token = extractToken(result);
    const nextUser = extractUser(result);

    if (!token) {
      throw new Error("Authentication token missing in register response.");
    }

    saveSession(token, nextUser);
    return nextUser;
  }

  function signOut() {
    clearBrowserSession();
    setUser(null);
  }

  const value = { user, isLoading, signIn, signUp, signOut };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside an AuthProvider.");
  }
  return context;
}
