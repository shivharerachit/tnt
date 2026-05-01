// LOGIN PAGE SCRIPT
// Handles user login

// If user is already logged in, send them to dashboard
redirectIfAlreadyLoggedIn();

// Get the HTML elements we need
var loginForm = document.getElementById("loginForm");
var emailInput = document.getElementById("email");
var messageBox = document.getElementById("msg");

// Check if there's a saved email from last login
var savedSession = loadSession();
if (savedSession && savedSession.email) {
  emailInput.value = savedSession.email;
}

// Handle login form submission
loginForm.addEventListener("submit", function(event) {
  // Prevent page from refreshing
  event.preventDefault();

  // Get email and password from form
  var email = emailInput.value.trim();
  var password = document.getElementById("password").value;

  // Call the API to login
  callAPI("/auth/login", {
    method: "POST",
    body: JSON.stringify({
      email: email,
      password: password
    })
  }).then(function(response) {
    // Extract user data from response
    var userData = extractData(response);

    // Save session to localStorage
    saveSession(userData);

    // Go to dashboard
    window.location.href = "dashboard.html";
  }).catch(function(error) {
    // Show error message to user
    showMessage("msg", error.message, true);
  });
});
