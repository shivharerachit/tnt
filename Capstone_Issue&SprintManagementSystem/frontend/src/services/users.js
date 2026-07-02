// Users service.
import { APP_CONFIG } from "../config/app-config";
import { apiClient } from "./api-client";

export async function getUsers(params = {}) {
  const query = new URLSearchParams();
  if (params.page) query.set("page", params.page);
  if (params.pageSize) query.set("pageSize", params.pageSize);
  if (params.search) query.set("search", params.search);
  const suffix = query.toString() ? `?${query.toString()}` : "";

  const result = await apiClient.get(`/users${suffix}`);
  return Array.isArray(result) ? result : result.items || [];
}

export async function getUser(userId) {
  return apiClient.get(`/users/${userId}`);
}

export async function updateUser(userId, data) {
  return apiClient.put(`/users/${userId}`, data);
}

export async function changeUserRole(userId, role) {
  return apiClient.patch(`/users/${userId}/role`, { role });
}

export async function deleteUser(userId) {
  return apiClient.remove(`/users/${userId}`);
}
