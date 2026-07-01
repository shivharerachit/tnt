// Authentication service.
import { APP_CONFIG } from "../config/app-config";
import { apiClient } from "./api-client";

export async function login(credentials) {
  return apiClient.post("/auth/login", credentials);
}

export async function register(data) {
  return apiClient.post("/auth/register", data);
}
