// COMMON HELPER FUNCTIONS
// Reusable functions used across all pages


// SESSION MANAGEMENT

// Try to load user session from localStorage
function loadSession() {
  var saved = localStorage.getItem("userSession");
  if (saved) {
    try {
      return JSON.parse(saved);
    } catch (error) {
      return null;
    }
  }
  return null;
}

// Save user session to localStorage
function saveSession(sessionData) {
  localStorage.setItem("userSession", JSON.stringify(sessionData));
}

// Clear user session (when logging out)
function clearSession() {
  localStorage.removeItem("userSession");
  window.location.href = "index.html";
}

// Check if user is logged in
function isLoggedIn() {
  var session = loadSession();
  return session && session.userId;
}

// Get current user's role (ADMIN, MANAGER, EMPLOYEE, etc)
function getUserRole() {
  var session = loadSession();
  if (session) {
    return session.role;
  }
  return "";
}

// Redirect to dashboard if user is already logged in
function redirectIfAlreadyLoggedIn() {
  if (isLoggedIn()) {
    window.location.href = "dashboard.html";
  }
}

// Redirect to login if user is not logged in
function redirectIfNotLoggedIn() {
  if (!isLoggedIn()) {
    localStorage.removeItem("userSession");
    window.location.href = "index.html";
  }
}

// API CALLS

// Make a request to the backend API
function callAPI(path, options) {
  var method = options && options.method ? options.method : "GET";
  var body = options && options.body ? options.body : null;
  var url = CONFIG.BACKEND_URL + path;

  // Build request headers
  var headers = {
    "Content-Type": "application/json"
  };

  // Add user ID to header if logged in
  var session = loadSession();
  if (session && session.userId) {
    headers["X-USER-ID"] = String(session.userId);
  }

  // Make the API call
  return fetch(url, {
    method: method,
    headers: headers,
    body: body
  }).then(function(response) {
    // Read the response as text
    return response.text().then(function(text) {
      var data = null;
      if (text) {
        try {
          data = JSON.parse(text);
        } catch (error) {
          throw new Error("Invalid response from server");
        }
      }

      // Check if there was an error
      if (!response.ok || (data && data.success === false)) {
        throw new Error(data && data.message ? data.message : "Request failed");
      }

      return data;
    });
  });
}

// Extract data from API response (handles nested data)
function extractData(response) {
  if (response && response.data) {
    return response.data;
  }
  return response;
}

// UI HELPERS 

// Display a message to the user (error or success)
function showMessage(elementId, text, isError) {
  var element = document.getElementById(elementId);
  if (element) {
    element.textContent = text;
    if (isError) {
      element.classList.add("error");
    } else {
      element.classList.remove("error");
    }
  }
}

// Format a number as USD currency (e.g., $1,234.56)
function formatMoney(amount) {
  var num = Number(amount || 0);
  return "$" + num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// Highlight the active page in navigation menu
function highlightActivePage(pageName) {
  var links = document.querySelectorAll(".nav a[data-page]");
  var i;
  for (i = 0; i < links.length; i++) {
    if (links[i].getAttribute("data-page") === pageName) {
      links[i].classList.add("active");
    }
  }
}

// Show or hide navigation menu items based on user role
function updateNavigationForRole() {
  var role = getUserRole();
  var usersLink = document.getElementById("navUsers");
  var reviewsLink = document.getElementById("navReviews");

  // Only ADMIN can see Users menu
  if (usersLink) {
    if (role === "ADMIN") {
      usersLink.classList.remove("hidden");
    } else {
      usersLink.classList.add("hidden");
    }
  }

  // Only ADMIN and MANAGER can see Reviews menu
  if (reviewsLink) {
    if (role === "ADMIN" || role === "MANAGER") {
      reviewsLink.classList.remove("hidden");
    } else {
      reviewsLink.classList.add("hidden");
    }
  }
}

// SHARED PAGE SETUP 

// This function is called by every page to set up common elements
function setupPage(activePage) {
  var session = loadSession();

  // Check if user is logged in
  if (!session || !session.userId) {
    window.location.href = "index.html";
    return;
  }

  // Set logged-in user info in the header
  var sessionLine = document.getElementById("sessionLine");
  if (sessionLine) {
    sessionLine.textContent = session.name + " | " + session.email + " | " + session.role;
  }

  // Highlight current page in navigation
  highlightActivePage(activePage);

  // Update navigation based on role
  updateNavigationForRole();

  // Set up logout button
  var logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", function() {
      clearSession();
    });
  }
}
