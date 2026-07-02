// Redirect to the login page when there is no authenticated user.
import { useEffect } from "react";
import { useNavigate } from "../lib/router";
import { useAuth } from "./useAuth";

export function useRequireAuth() {
  const { user, isLoading } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoading && !user) {
      navigate({ to: "/login" });
    }
  }, [user, isLoading, navigate]);

  return { user, isLoading };
}
