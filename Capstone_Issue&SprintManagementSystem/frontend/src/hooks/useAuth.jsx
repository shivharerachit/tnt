// Authentication context + hook. Stores the current user/token and exposes
// sign-in, sign-up and sign-out helpers to the whole app.
import { createContext, useContext, useEffect, useState } from "react";
import { STORAGE_KEYS } from "../constants";
import * as authService from "../services/auth";

const AuthContext = createContext(null);

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
    saveSession(result.token, result.user);
    return result.user;
  }

  async function signUp(data) {
    const result = await authService.register(data);
    saveSession(result.token, result.user);
    return result.user;
  }

  function signOut() {
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
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
