// Generic helper for loading data from a service function.
// Handles the loading / error / data states in one place so pages stay clean.
import { useCallback, useEffect, useState } from "react";

export function useAsyncData(asyncFunction, deps = []) {
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

  const run = useCallback(async () => {
    setIsLoading(true);
    setError("");
    try {
      const result = await asyncFunction();
      setData(result);
    } catch (err) {
      setError(err.message || "Something went wrong.");
    } finally {
      setIsLoading(false);
    }
  }, deps);

  useEffect(() => {
    run();
  }, [run]);

  return { data, isLoading, error, reload: run };
}
