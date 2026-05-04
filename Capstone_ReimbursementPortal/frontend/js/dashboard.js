// DASHBOARD PAGE SCRIPT
// Shows summary statistics

// Check if user is logged in and set up page
setupPage("dashboard");

// Get the HTML elements
var messageBox = document.getElementById("msg");
var usersCountElement = document.getElementById("usersCount");
var claimsCountElement = document.getElementById("claimsCount");
var pendingCountElement = document.getElementById("pendingCount");

// Find the correct API path based on user role
function getAPIPathForRole() {
  var role = getUserRole();
  var userId = loadSession().userId;

  if (role === "ADMIN") {
    return {
      users: "/users",
      claims: "/claims/all/paginated?page=0&size=20"
    };
  } else if (role === "MANAGER") {
    return {
      users: "/users/manager/" + userId,
      claims: "/claims/reviewer/paginated?page=0&size=20"
    };
  } else {
    // EMPLOYEE
    return {
      users: null,
      claims: "/claims/my/paginated?page=0&size=20"
    };
  }
}

// Load and display dashboard data
function loadDashboard() {
  var paths = getAPIPathForRole();
  var role = getUserRole();

  // For employees, just show 1 user (themselves)
  var usersPromise;
  if (role === "EMPLOYEE") {
    usersPromise = Promise.resolve(1);
  } else {
    // For admins and managers, fetch the user count
    usersPromise = callAPI(paths.users).then(function(response) {
      var users = extractData(response);
      return users ? users.length : 0;
    });
  }

  // Fetch claims data
  var claimsPromise = callAPI(paths.claims).then(function(response) {
    return extractData(response);
  });

  // Wait for both API calls to complete
  Promise.all([usersPromise, claimsPromise]).then(function(results) {
    var userCount = results[0];
    var claimsData = results[1];
    var claimsList = claimsData.content || [];

    // Count how many claims are still pending
    var pendingCount = 0;
    var i;
    for (i = 0; i < claimsList.length; i++) {
      if (claimsList[i].status === "SUBMITTED") {
        pendingCount = pendingCount + 1;
      }
    }

    // Update the display
    usersCountElement.textContent = String(userCount);
    claimsCountElement.textContent = String(claimsData.totalElements || claimsList.length);
    pendingCountElement.textContent = String(pendingCount);

    showMessage("msg", "Dashboard loaded.", false);
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    // If there's an auth error, redirect to login
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
}

// Load dashboard when page loads
loadDashboard();
