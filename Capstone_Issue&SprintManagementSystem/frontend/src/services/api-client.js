import { APP_CONFIG } from "../config/app-config";
import { STORAGE_KEYS } from "../constants";

function clearBrowserSession() {
  localStorage.clear();
  sessionStorage.clear();
}

function redirectToLogin() {
  if (window.location.pathname !== "/login") {
    window.location.replace("/login");
  }
}

async function apiRequest(path, options = {}) {
  const token = localStorage.getItem(STORAGE_KEYS.TOKEN);

  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  let response;
  try {
    response = await fetch(`${APP_CONFIG.API_BASE_URL}${path}`, {
      ...options,
      headers,
      body: options.body ? JSON.stringify(options.body) : undefined,
    });
  } catch (networkError) {
    throw new Error("Unable to reach the server. Please try again.");
  }

  let data = null;
  const text = await response.text();
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    const message =
      (data && (data.detail || data.message)) ||
      `Request failed with status ${response.status}`;

    if (response.status === 401) {
      clearBrowserSession();
      redirectToLogin();
    }

    throw new Error(message);
  }

  return data;
}

export const apiClient = {
  get: (path) => apiRequest(path, { method: "GET" }),
  post: (path, body) => apiRequest(path, { method: "POST", body }),
  put: (path, body) => apiRequest(path, { method: "PUT", body }),
  patch: (path, body) => apiRequest(path, { method: "PATCH", body }),
  remove: (path) => apiRequest(path, { method: "DELETE" }),
};
