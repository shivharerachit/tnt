// Small validation helpers reused across forms.

export function isEmail(value) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(value || "").trim());
}

export function isRequired(value) {
  return String(value || "").trim().length > 0;
}

export function minLength(value, length) {
  return String(value || "").trim().length >= length;
}

export function hasPasswordComplexity(value) {
  return /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).+$/.test(String(value || ""));
}

export function validateName(value) {
  if (!isRequired(value)) {
    return "Name is required.";
  }

  if (!minLength(value, 2)) {
    return "Name must be at least 2 characters long.";
  }

  return "";
}

export function validateEmail(value) {
  if (!isRequired(value)) {
    return "Email is required.";
  }

  if (!isEmail(value)) {
    return "Please enter a valid email address.";
  }

  return "";
}

export function validatePassword(value) {
  const password = String(value || "");

  if (!isRequired(password)) {
    return "Password is required.";
  }

  if (!minLength(password, 6)) {
    return "Password must be at least 6 characters long.";
  }

  if (!/[A-Z]/.test(password)) {
    return "Password must contain one uppercase letter.";
  }

  if (!/[a-z]/.test(password)) {
    return "Password must contain one lowercase letter.";
  }

  if (!/[0-9]/.test(password)) {
    return "Password must contain one digit.";
  }

  if (!/[^A-Za-z0-9]/.test(password)) {
    return "Password must contain one special character.";
  }

  return "";
}

export function validateRole(value) {
  if (!isRequired(value)) {
    return "Role is required.";
  }

  return "";
}
