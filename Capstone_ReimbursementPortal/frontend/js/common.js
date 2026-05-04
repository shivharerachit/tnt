// COMMON HELPER FUNCTIONS
// Clean, minimal implementations used by pages (setup, API calls, UI helpers)

// Load / save session
function loadSession() {
  let saved = localStorage.getItem('userSession');
  if (!saved) return null;
  try {
    return JSON.parse(saved);
  } catch (e) {
    return null;
  }
}

function saveSession(sessionData) {
  localStorage.setItem('userSession', JSON.stringify(sessionData));
}

function clearSession() {
  localStorage.removeItem('userSession');
  window.location.href = 'index.html';
}

function isLoggedIn() {
  let s = loadSession();
  return s && s.userId;
}

function getUserRole() {
  let s = loadSession();
  return s && s.role ? s.role : '';
}

// Redirect to dashboard if already logged in (for login page)
function redirectIfAlreadyLoggedIn() {
  if (isLoggedIn()) {
    window.location.href = 'dashboard.html';
  }
}

// Redirect to login if not logged in (for protected pages)
function redirectIfNotLoggedIn() {
  if (!isLoggedIn()) {
    clearSession();
    window.location.href = 'index.html';
  }
}

// Call this from pages to perform common setup tasks
function setupPage(pageName) {
  let session = loadSession();

  // If not logged in, redirect to login for pages that expect auth
  // (pages can perform their own checks too)
  // Do not force redirect here to allow public pages

  // Fill sidebar user info if present
  let sidebarUser = document.getElementById('sidebarUser');
  if (sidebarUser && session) {
    sidebarUser.textContent = session.name + ' (' + session.role + ')';
  }

  // Fill session line if present (for backward compatibility)
  let sessionLine = document.getElementById('sessionLine');
  if (sessionLine && session) {
    sessionLine.textContent = session.name + ' | ' + session.email + ' | ' + session.role;
  }

  // Highlight active navigation links (if they use data-page)
  try {
    let links = document.querySelectorAll('[data-page]');
    for (let i = 0; i < links.length; i++) {
      if (links[i].getAttribute('data-page') === pageName) {
        links[i].classList.add('active');
      } else {
        links[i].classList.remove('active');
      }
    }
  } catch (e) {}

  // Show/hide Users link based on role
  let usersLink = document.getElementById('navUsers');
  if (usersLink) {
    if (getUserRole() === 'ADMIN') usersLink.classList.remove('hidden');
    else usersLink.classList.add('hidden');
  }

  // Wire up logout button
  let logoutBtn = document.getElementById('logoutBtn');
  if (logoutBtn) {
    logoutBtn.addEventListener('click', function() {
      clearSession();
    });
  }

  // Modal overlay click-to-close behavior
  document.querySelectorAll('.modal-overlay').forEach(function(modal) {
    modal.addEventListener('click', function(e) {
      if (e.target === modal) {
        modal.style.display = 'none';
        modal.classList.remove('active');
      }
    });
  });
}

// Simple API wrapper
function callAPI(path, options) {
  options = options || {};
  let method = options.method || 'GET';
  let body = options.body || null;
  let url = CONFIG.BACKEND_URL + path;

  let headers = options.headers || {};
  if (!headers['Content-Type'] && !(body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }
  // Include X-USER-ID header if user session exists (backend requires this)
  try {
    let sess = loadSession();
    if (sess && sess.userId) {
      headers['X-USER-ID'] = String(sess.userId);
    }
  } catch (e) {}

  return fetch(url, {
    method: method,
    headers: headers,
    body: body
  }).then(function(response) {
    return response.text().then(function(text) {
      let data = null;
      if (text) {
        try {
          data = JSON.parse(text);
        } catch (e) {
          // not JSON, return raw text
          data = text;
        }
      }

      if (!response.ok) {
        let msg = (data && data.message) ? data.message : ('Request failed: ' + response.status);
        throw new Error(msg);
      }

      return data;
    });
  });
}

// Extract common response data
function extractData(response) {
  if (!response) return null;
  if (response.data !== undefined) return response.data;
  if (response.content !== undefined) return response;
  return response;
}

// UI helpers
function showMessage(elementId, text, isError) {
  let el = document.getElementById(elementId);
  if (!el) return;
  el.textContent = text;
  el.classList.remove('hidden');
  if (isError) {
    el.classList.add('error');
  } else {
    el.classList.remove('error');
  }
  // Auto-hide after 4 seconds for non-error messages
  if (!isError) {
    setTimeout(function() { el.classList.add('hidden'); }, 4000);
  }
}

function formatMoney(num) {
  if (!num && num !== 0) return '₹0.00';
  return '₹' + Number(num).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

// --- Client-side email & password checks (align with backend where possible) ---
/* Strictly more than 6 characters → minimum length 7 */
const VALIDATION_PASSWORD_MIN_CHARS = 7;
const VALIDATION_PASSWORD_MAX = 100;

/**
 * Generic shape: one @, non-empty local part, domain with at least one dot (e.g. name@company.com).
 * Does NOT check allowed domain — only the backend should do that.
 */
function isGenericEmailPattern(email) {
  if (!email || typeof email !== 'string') return false;
  let e = email.trim();
  if (e.length < 5 || e.length > 254) return false;
  if (/\s/.test(e)) return false;
  let at = e.indexOf('@');
  if (at < 1) return false;
  if (e.indexOf('@', at + 1) !== -1) return false;
  let local = e.slice(0, at);
  let domain = e.slice(at + 1);
  if (!domain.length) return false;
  if (domain.indexOf('.') === -1) return false;
  if (domain.charAt(0) === '.' || domain.charAt(domain.length - 1) === '.') return false;
  return true;
}

/**
 * Email format on the frontend; domain rules are backend-only.
 * @returns {string|null} error message, or null if OK
 */
function validateEmail(email) {
  if (email == null || String(email).trim() === '') {
    return 'Please enter your email address.';
  }
  let trimmed = String(email).trim();
  if (!isGenericEmailPattern(trimmed)) {
    return 'Please enter a valid email address (for example name@example.com).';
  }
  return null;
}

/**
 * Password: length only — more than 6 characters (7+), max 100. No letter/symbol rules.
 */
function validatePassword(password) {
  if (password == null || typeof password !== 'string') {
    return 'Please enter your password.';
  }
  if (password.length === 0) {
    return 'Please enter your password.';
  }
  if (password.length < VALIDATION_PASSWORD_MIN_CHARS) {
    return 'Password must be more than 6 characters.';
  }
  if (password.length > VALIDATION_PASSWORD_MAX) {
    return (
      'Password must be no more than ' +
      VALIDATION_PASSWORD_MAX +
      ' characters.'
    );
  }
  return null;
}

// Modal helpers
function openModal(id) {
  let m = document.getElementById(id);
  if (!m) return;
  m.style.display = 'flex';
  m.classList.add('active');
}

function closeModal(id) {
  let m = document.getElementById(id);
  if (!m) return;
  m.style.display = 'none';
  m.classList.remove('active');
}
