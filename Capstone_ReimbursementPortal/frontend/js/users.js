// USERS PAGE SCRIPT (ADMIN ONLY)
// Manage users, assign managers, view teams

// Check if user is logged in and set up page
setupPage("users");

// Only admins can access this page
var role = getUserRole();
if (role !== "ADMIN") {
  window.location.href = "dashboard.html";
}

// Get HTML elements
var messageBox = document.getElementById("msg");
var usersTableBody = document.getElementById("usersBody");
var filterRoleSelect = document.getElementById("filterRole");
var createForm = document.getElementById("createForm");
var assignForm = document.getElementById("assignForm");
var teamForm = document.getElementById("teamForm");
var teamList = document.getElementById("teamList");

// Store all users for reference
var allUsers = [];

// Find user name by ID
function findUserName(userId) {
  var i;
  for (i = 0; i < allUsers.length; i++) {
    if (String(allUsers[i].id) === String(userId)) {
      return allUsers[i].name;
    }
  }
  return "-";
}

// Create HTML options for manager dropdowns
function populateManagerSelects(users) {
  var managers = [];
  var employees = [];

  // Separate users by role
  var i;
  for (i = 0; i < users.length; i++) {
    if (users[i].role === "MANAGER" || users[i].role === "ADMIN") {
      managers.push(users[i]);
    } else if (users[i].role === "EMPLOYEE") {
      employees.push(users[i]);
    }
  }

  // Build HTML for manager options
  var managerHTML = '<option value="">No manager</option>';
  var assignManagerHTML = '';
  var teamManagerHTML = '';

  for (i = 0; i < managers.length; i++) {
    var option = '<option value="' + managers[i].id + '">' + managers[i].name + '</option>';
    managerHTML = managerHTML + option;
    assignManagerHTML = assignManagerHTML + option;
    teamManagerHTML = teamManagerHTML + option;
  }

  // Update the selects
  document.getElementById("managerId").innerHTML = managerHTML;
  document.getElementById("assignManagerId").innerHTML = assignManagerHTML;
  document.getElementById("teamManagerId").innerHTML = teamManagerHTML;

  // Build HTML for employee options
  var employeeHTML = '';
  for (i = 0; i < employees.length; i++) {
    employeeHTML = employeeHTML + '<option value="' + employees[i].id + '">' + employees[i].name + '</option>';
  }

  document.getElementById("employeeId").innerHTML = employeeHTML;
}

// Display users in the table
function displayUsers(users) {
  // If no users, show a message
  if (users.length === 0) {
    usersTableBody.innerHTML = '<tr><td colspan="6">No users found.</td></tr>';
    return;
  }

  // Build HTML for each user row
  var html = "";
  var i;
  for (i = 0; i < users.length; i++) {
    var user = users[i];
    html = html + '<tr>' +
      '<td>' + user.id + '</td>' +
      '<td>' + user.name + '</td>' +
      '<td>' + user.email + '</td>' +
      '<td>' + user.role + '</td>' +
      '<td>' + (user.managerId ? findUserName(user.managerId) : '-') + '</td>' +
      '<td><button class="btn btn-danger" data-user-id="' + user.id + '" type="button">Delete</button></td>' +
      '</tr>';
  }

  // Update the table
  usersTableBody.innerHTML = html;

  // Add click handlers to delete buttons
  var deleteButtons = document.querySelectorAll("button[data-user-id]");
  var j;
  for (j = 0; j < deleteButtons.length; j++) {
    deleteButtons[j].addEventListener("click", function() {
      var userId = this.getAttribute("data-user-id");
      handleDeleteUser(userId);
    });
  }
}

// Delete a user
function handleDeleteUser(userId) {
  var confirmed = confirm("Delete user #" + userId + "?");

  if (!confirmed) {
    return;
  }

  callAPI("/users/" + userId, {
    method: "DELETE"
  }).then(function() {
    loadUsers();
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
}

// Load users from API
function loadUsers() {
  // First, load all users to populate the dropdowns
  callAPI("/users").then(function(response) {
    allUsers = extractData(response) || [];
    populateManagerSelects(allUsers);

    // Then load filtered users
    var selectedRole = filterRoleSelect.value;
    var path = "/users";

    if (selectedRole !== "ALL") {
      path = "/users/by-role?roleParam=" + encodeURIComponent(selectedRole);
    }

    return callAPI(path);
  }).then(function(response) {
    var users = extractData(response) || [];
    displayUsers(users);
    showMessage("msg", "Users loaded.", false);
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
}

// Handle role filter change
filterRoleSelect.addEventListener("change", function() {
  loadUsers();
});

// Handle create user form submission
createForm.addEventListener("submit", function(event) {
  event.preventDefault();

  var email = document.getElementById("email").value.trim();
  var role = document.getElementById("role").value;
  var managerId = document.getElementById("managerId").value;
  var allowedDomain = CONFIG.ALLOWED_DOMAIN;

  // Check if email matches the allowed domain
  if (!email.toLowerCase().endsWith(allowedDomain.toLowerCase())) {
    showMessage("msg", "Email must match configured domain.", true);
    return;
  }

  // Prepare the user data
  var userData = {
    name: document.getElementById("name").value.trim(),
    email: email,
    password: document.getElementById("password").value,
    role: role,
    managerId: null
  };

  // Only set managerId for employees who have a manager
  if (role === "EMPLOYEE" && managerId) {
    userData.managerId = Number(managerId);
  }

  // Submit the form
  callAPI("/users", {
    method: "POST",
    body: JSON.stringify(userData)
  }).then(function() {
    createForm.reset();
    loadUsers();
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
});

// Handle assign manager form submission
assignForm.addEventListener("submit", function(event) {
  event.preventDefault();

  var employeeId = document.getElementById("employeeId").value;
  var managerId = document.getElementById("assignManagerId").value;

  callAPI("/users/" + employeeId + "/assign-manager?managerId=" + managerId, {
    method: "PUT"
  }).then(function() {
    loadUsers();
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
});

// Handle team view form submission
teamForm.addEventListener("submit", function(event) {
  event.preventDefault();

  var managerId = document.getElementById("teamManagerId").value;

  callAPI("/users/manager/" + managerId).then(function(response) {
    var users = extractData(response) || [];

    if (users.length === 0) {
      teamList.innerHTML = "<li>No users under this manager.</li>";
      return;
    }

    // Build list of users
    var html = "";
    var i;
    for (i = 0; i < users.length; i++) {
      html = html + "<li>" + users[i].name + " - " + users[i].email + "</li>";
    }

    teamList.innerHTML = html;
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
});

// Load users when page loads
loadUsers();
