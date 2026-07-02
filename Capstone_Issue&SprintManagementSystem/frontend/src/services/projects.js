// Projects service.
import { APP_CONFIG } from "../config/app-config";
import { apiClient } from "./api-client";

export async function getProjects() {
  return apiClient.get("/projects");
}

export async function getProject(projectId) {
  return apiClient.get(`/projects/${projectId}`);
}

export async function createProject(data) {
  return apiClient.post("/projects", data);
}

export async function updateProjectMembers(projectId, memberIds) {
  return apiClient.put(`/projects/${projectId}/members`, { memberIds });
}
