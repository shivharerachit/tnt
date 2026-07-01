import { STORAGE_KEYS } from "../constants";

function clearBrowserSession() {
  localStorage.clear();
  sessionStorage.clear();
}

export function isAuthenticated() {
  return Boolean(localStorage.getItem(STORAGE_KEYS.TOKEN));
}

export function logout() {
  clearBrowserSession();
  window.location.reload();
}
