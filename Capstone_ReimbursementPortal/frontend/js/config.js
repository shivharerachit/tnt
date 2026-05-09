// CONFIGURATION FILE
// This file stores all settings needed by the app

// Global config (object is frozen at reference level; mutate fields if you must)
const CONFIG = {
  BACKEND_URL: "http://localhost:8080",
  CLAIM_LIMIT: 50000
};

// UserRole values — must match backend
const ROLE = Object.freeze({
  ADMIN: "ADMIN",
  MANAGER: "MANAGER",
  EMPLOYEE: "EMPLOYEE"
});

// Claim status enum — must match backend and filter `<option value>` where used
const CLAIM_STATUS = Object.freeze({
  SUBMITTED: "SUBMITTED",
  APPROVED: "APPROVED",
  REJECTED: "REJECTED"
});

// Dashboard copy branch keys (not API roles)
const DASHBOARD_SCOPE = Object.freeze({
  ADMIN: "admin",
  MANAGER: "manager",
  EMPLOYEE: "employee"
});
