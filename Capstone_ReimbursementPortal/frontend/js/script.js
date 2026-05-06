// LOGIN PAGE SCRIPT
// Handles user login

// If user is already logged in, send them to dashboard
redirectIfAlreadyLoggedIn();

// Get the HTML elements we need
let loginForm = document.getElementById("loginForm");
let emailInput = document.getElementById("email");
let messageBox = document.getElementById("msg");

// Check if there's a saved email from last login
let savedSession = loadSession();
if (savedSession && savedSession.email) {
  emailInput.value = savedSession.email;
}

// Handle login form submission
loginForm.addEventListener("submit", function(event) {
  // Prevent page from refreshing
  event.preventDefault();

  // Get email and password from form
  let email = emailInput.value.trim();
  let password = document.getElementById("password").value;

  let emailError = validateEmail(email);
  if (emailError) {
    showMessage("msg", emailError, true);
    return;
  }

  let passwordError = validatePassword(password);
  if (passwordError) {
    showMessage("msg", passwordError, true);
    return;
  }

  // Call the API to login
  callAPI("/auth/login", {
    method: "POST",
    body: JSON.stringify({
      email: email,
      password: password
    })
  }).then(function(response) {
    // Extract user data from response
    let userData = extractData(response);

    // Save session to localStorage
    saveSession(userData);

    // Go to dashboard
    window.location.href = "dashboard.html";
  }).catch(function(error) {
    // Show error message to user
    showMessage("msg", error.message, true);
  });
});
