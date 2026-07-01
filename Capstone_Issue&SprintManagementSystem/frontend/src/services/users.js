// Users service.
import { APP_CONFIG } from "../config/app-config";
import { apiClient } from "./api-client";

export async function getUsers() {
  return apiClient.get("/users");
}
