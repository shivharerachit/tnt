// USERS PAGE SCRIPT (ADMIN ONLY)
// Manage users, assign managers, view teams

// Check if user is logged in and set up page
setupPage("users");

// Only admins can access this page
let role = getUserRole();
if (role !== "ADMIN") {
  window.location.href = "dashboard.html";
}

// Get HTML elements
let messageBox = document.getElementById("msg");
let usersTableBody = document.getElementById("usersBody");
let filterRoleSelect = document.getElementById("filterRole");
let createForm = document.getElementById("createForm");
let newUserBtn = document.getElementById("newUserBtn");
let searchInput = document.getElementById("searchInput");
let filterManagerSelect = document.getElementById("filterManager");

// Sorting state
let sortColumn = null;
let sortAsc = true;

// Managers cache and options HTML used for inline selects
let managersList = [];
let managerOptionsHTML = '';

// Store all users for reference
let allUsers = [];

// Find user name by ID
function findUserName(userId) {
  let i;
  for (i = 0; i < allUsers.length; i++) {
    if (String(allUsers[i].id) === String(userId)) {
      return allUsers[i].name;
    }
  }
  return "-";
}

// Create HTML options for manager dropdowns
function populateManagerSelects(users) {
  let managers = [];

  // Separate users by role
  let i;
  for (i = 0; i < users.length; i++) {
    if (users[i].role === "MANAGER" || users[i].role === "ADMIN") {
      managers.push(users[i]);
    }
  }

  // Build HTML for manager options
  let managerHTML = '<option value="">No manager</option>';

  for (i = 0; i < managers.length; i++) {
    let option = '<option value="' + managers[i].id + '">' + managers[i].name + '</option>';
    managerHTML = managerHTML + option;
  }

  // Update the selects
  let el = document.getElementById("managerId"); if (el) el.innerHTML = managerHTML;

  // Cache managers and manager options for inline use
  managersList = managers;
  managerOptionsHTML = managerHTML;

  // Populate the manager filter select if present
  if (filterManagerSelect) {
    let fmHTML = '<option value="">All managers</option>' + managerHTML;
    filterManagerSelect.innerHTML = fmHTML;
  }
}

// Display users in the table
function displayUsers(users) {
  // If no users, show a message
  if (!users || users.length === 0) {
    usersTableBody.innerHTML = '<tr><td colspan="6">No users found.</td></tr>';
    return;
  }

  // Build HTML for each user row with inline manager selector
  let html = "";
  let i;
  for (i = 0; i < users.length; i++) {
    let user = users[i];
    // Build manager cell: for employees show a select, otherwise show manager name or '-'
    let managerCell = '-';
    if (user.role === 'EMPLOYEE') {
      let opts = managerOptionsHTML || '<option value="">No manager</option>';
      // Build options but mark selected
      let optsSelected = '';
      if (managersList && managersList.length > 0) {
        optsSelected = '<option value="">No manager</option>';
        for (let m = 0; m < managersList.length; m++) {
          let mo = managersList[m];
          let sel = String(mo.id) === String(user.managerId) ? ' selected' : '';
          optsSelected += '<option value="' + mo.id + '"' + sel + '>' + mo.name + '</option>';
        }
      } else {
        optsSelected = opts;
      }
      managerCell = '<select class="manager-select" data-user-id="' + user.id + '">' + optsSelected + '</select>';
    } else {
      managerCell = user.managerId ? findUserName(user.managerId) : '-';
    }

    html += '<tr>' +
      '<td>' + user.id + '</td>' +
      '<td>' + user.name + '</td>' +
      '<td>' + user.email + '</td>' +
      '<td>' + user.role + '</td>' +
      '<td>' + managerCell + '</td>' +
      '<td>' +
        '<button class="btn btn-secondary btn-sm" data-user-id="' + user.id + '" data-action="view" type="button">View</button> ' +
        '<button class="btn btn-danger btn-sm" data-user-id="' + user.id + '" data-action="delete" type="button">Delete</button>' +
      '</td>' +
      '</tr>';
  }

  // Update the table
  usersTableBody.innerHTML = html;

  // Add click handlers to action buttons
  let actionButtons = document.querySelectorAll("button[data-user-id]");
  for (let j = 0; j < actionButtons.length; j++) {
    actionButtons[j].addEventListener("click", function() {
      let userId = this.getAttribute("data-user-id");
      let action = this.getAttribute("data-action");
      if (action === "delete") {
        handleDeleteUser(userId);
      } else if (action === "view") {
        viewUserDetails(userId);
      }
    });
  }

  // Add change handlers to inline manager selects
  let managerSelects = document.querySelectorAll('.manager-select');
  for (let k = 0; k < managerSelects.length; k++) {
    managerSelects[k].addEventListener('change', function() {
      let userId = this.getAttribute('data-user-id');
      let managerId = this.value;
      callAPI('/users/' + userId + '/assign-manager?managerId=' + managerId, { method: 'PUT' })
        .then(function() {
          loadUsers();
        }).catch(function(error) {
          showMessage('msg', error.message, true);
        });
    });
  }
}

// Delete a user
function handleDeleteUser(userId) {
  let confirmed = confirm("Delete user #" + userId + "?");

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
  // Load all users (we will filter/sort client-side)
  callAPI("/users").then(function(response) {
    allUsers = extractData(response) || [];
    populateManagerSelects(allUsers);
    renderUsers();
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
}

// Build filtered and sorted list and display
function renderUsers() {
  let filtered = allUsers.slice();

  // Role filter
  let roleVal = filterRoleSelect ? filterRoleSelect.value : '';
  if (roleVal) {
    filtered = filtered.filter(function(u) { return u.role === roleVal; });
  }

  // Manager filter (show employees under a manager)
  if (filterManagerSelect && filterManagerSelect.value) {
    let mid = filterManagerSelect.value;
    filtered = filtered.filter(function(u) { return String(u.managerId) === String(mid); });
  }

  // Search filter
  if (searchInput && searchInput.value.trim() !== '') {
    let q = searchInput.value.trim().toLowerCase();
    filtered = filtered.filter(function(u) {
      return String(u.id).indexOf(q) !== -1 || (u.name && u.name.toLowerCase().indexOf(q) !== -1) || (u.email && u.email.toLowerCase().indexOf(q) !== -1);
    });
  }

  // Sorting
  if (sortColumn) {
    filtered.sort(function(a, b) {
      let va = a[sortColumn] || '';
      let vb = b[sortColumn] || '';
      // numeric sort for id
      if (sortColumn === 'id') {
        return sortAsc ? (Number(va) - Number(vb)) : (Number(vb) - Number(va));
      }
      va = String(va).toLowerCase();
      vb = String(vb).toLowerCase();
      if (va < vb) return sortAsc ? -1 : 1;
      if (va > vb) return sortAsc ? 1 : -1;
      return 0;
    });
  }

  displayUsers(filtered);
}

// Handle role filter change
if (filterRoleSelect) {
  filterRoleSelect.addEventListener("change", function() {
    renderUsers();
  });
}

// Search input
if (searchInput) {
  searchInput.addEventListener('input', function() {
    renderUsers();
  });
}

// Manager filter
if (filterManagerSelect) {
  filterManagerSelect.addEventListener('change', function() {
    renderUsers();
  });
}

// Sortable table headers
document.querySelectorAll('th.sortable').forEach(function(th) {
  th.addEventListener('click', function() {
    let col = th.getAttribute('data-sort');
    if (sortColumn === col) {
      sortAsc = !sortAsc;
    } else {
      sortColumn = col;
      sortAsc = true;
    }
    renderUsers();
  });
});

// Handle new user button click (if present)
if (newUserBtn) {
  newUserBtn.addEventListener("click", function() {
    openModal('createUserModal');
  });
}

// Create user function (called from modal button)
function createUser() {
  document.getElementById('createForm').dispatchEvent(new Event('submit'));
}

// Handle create user form submission
createForm.addEventListener("submit", function(event) {
  event.preventDefault();

  let email = document.getElementById("email").value.trim();
  let role = document.getElementById("role").value;
  let managerId = document.getElementById("managerId").value;
  let rawPassword = document.getElementById("password").value;

  let emailError = validateEmail(email);
  if (emailError) {
    showMessage("msg", emailError, true);
    return;
  }

  let passwordError = validatePassword(rawPassword);
  if (passwordError) {
    showMessage("msg", passwordError, true);
    return;
  }

  // Prepare the user data
  let userData = {
    name: document.getElementById("name").value.trim(),
    email: email,
    password: rawPassword,
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
    closeModal('createUserModal');
    loadUsers();
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
});

// View user details
function viewUserDetails(userId) {
  let user = allUsers.find(function(u) { return String(u.id) === String(userId); });
  if (!user) {
    showMessage("msg", "User not found.", true);
    return;
  }

  let detailsHTML = '<p><strong>ID:</strong> ' + user.id + '</p>' +
    '<p><strong>Name:</strong> ' + user.name + '</p>' +
    '<p><strong>Email:</strong> ' + user.email + '</p>' +
    '<p><strong>Role:</strong> ' + user.role + '</p>' +
    '<p><strong>Manager:</strong> ' + (user.managerId ? findUserName(user.managerId) : 'None') + '</p>';

  document.getElementById('userDetails').innerHTML = detailsHTML;
  openModal('viewUserModal');
}

// Load users when page loads
loadUsers();
