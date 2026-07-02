// Page wrapper for authenticated screens: guards auth and renders the navbar.
import Navbar from "./Navbar";
import { LoadingState } from "./DataStates";
import { useRequireAuth } from "../hooks/useRequireAuth";

export default function AppLayout({ children }) {
  const { user, isLoading } = useRequireAuth();

  if (isLoading || !user) {
    return (
      <div className="center-screen">
        <LoadingState message="Checking your session..." />
      </div>
    );
  }

  return (
    <>
      <Navbar />
      <main className="page">{children}</main>
    </>
  );
}
