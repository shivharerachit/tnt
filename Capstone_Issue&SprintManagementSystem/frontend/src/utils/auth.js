import { STORAGE_KEYS } from "../constants";

export function isAuthenticated() {
  return Boolean(localStorage.getItem(STORAGE_KEYS.TOKEN));
}

export function logout() {
  localStorage.removeItem(STORAGE_KEYS.TOKEN);
  window.location.reload();
}
