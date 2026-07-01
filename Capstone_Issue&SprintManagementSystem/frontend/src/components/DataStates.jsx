// Loading, error and empty state components. Reused everywhere data is fetched.

export function LoadingState({ message = "Loading..." }) {
  return (
    <div className="state-box" role="status" aria-live="polite">
      <div className="spinner" aria-hidden="true" />
      <p>{message}</p>
    </div>
  );
}
