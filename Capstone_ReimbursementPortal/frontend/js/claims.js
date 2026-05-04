// CLAIMS PAGE SCRIPT
// Shows claims and allows submitting new ones


// Check if user is logged in and set up page
setupPage("claims");

// Get HTML elements
var messageBox = document.getElementById("msg");
var claimsTableBody = document.getElementById("claimsBody");
var pageInfoElement = document.getElementById("pageInfo");
var sizeSelect = document.getElementById("size");
var prevButton = document.getElementById("prev");
var nextButton = document.getElementById("next");
var claimForm = document.getElementById("claimForm");
var submitCard = document.getElementById("submitCard");

// Variables to remember state
var allUsers = [];
var currentPage = 1;
var totalPages = 1;

// Get the correct API path based on user role
function getClaimsPath() {
  var role = getUserRole();

  if (role === "ADMIN") {
    return "/claims/all/paginated";
  } else if (role === "MANAGER") {
    return "/claims/reviewer/paginated";
  } else {
    // EMPLOYEE
    return "/claims/my/paginated";
  }
}

// Get the correct users API path based on user role
function getUsersPath() {
  var role = getUserRole();
  var session = loadSession();

  if (role === "ADMIN") {
    return "/users";
  } else if (role === "MANAGER") {
    return "/users/manager/" + session.userId;
  } else {
    // EMPLOYEE - no API call needed
    return null;
  }
}

// Find a user name by their ID
function getUserName(userId) {
  var session = loadSession();

  // If it's the current user, use their name
  if (String(userId) === String(session.userId)) {
    return session.name;
  }

  // Otherwise, search in the users list
  var i;
  for (i = 0; i < allUsers.length; i++) {
    if (String(allUsers[i].id) === String(userId)) {
      return allUsers[i].name;
    }
  }

  // If not found, show the ID
  return "#" + userId;
}

// Display claims in the table
function displayClaims(claims) {
  // If no claims, show a message
  if (claims.length === 0) {
    claimsTableBody.innerHTML = '<tr><td colspan="6">No claims found.</td></tr>';
    return;
  }

  // Build HTML for each claim row
  var html = "";
  var i;
  for (i = 0; i < claims.length; i++) {
    var claim = claims[i];
    html = html + '<tr>' +
      '<td>' + claim.id + '</td>' +
      '<td>' + getUserName(claim.employeeId) + '</td>' +
      '<td>' + formatMoney(claim.amount) + '</td>' +
      '<td><span class="badge ' + claim.status + '">' + claim.status + '</span></td>' +
      '<td>' + (claim.reviewerId ? getUserName(claim.reviewerId) : '-') + '</td>' +
      '<td>' + (claim.comments || '-') + '</td>' +
      '</tr>';
  }

  // Update the table
  claimsTableBody.innerHTML = html;
}

// Load claims from the API
function loadClaims() {
  var pageSize = Number(sizeSelect.value);
  var claimsPath = getClaimsPath();
  var path = claimsPath + "?page=" + (currentPage - 1) + "&size=" + pageSize;

  callAPI(path).then(function(response) {
    var claimsData = extractData(response);

    // Update page info
    if (claimsData) {
      totalPages = claimsData.totalPages || 1;
      currentPage = (claimsData.number || 0) + 1;
      pageInfoElement.textContent = "Page " + currentPage + " of " + totalPages;
      displayClaims(claimsData.content || []);
    } else {
      displayClaims([]);
    }

    showMessage("msg", "Claims loaded.", false);
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
}

// Load users list from API
function loadUsers() {
  var usersPath = getUsersPath();
  var session = loadSession();

  // For employees, just use their own info
  if (!usersPath) {
    allUsers = [{
      id: session.userId,
      name: session.name
    }];
    return Promise.resolve();
  }

  // For admins and managers, fetch the users
  return callAPI(usersPath).then(function(response) {
    allUsers = extractData(response) || [];
  });
}

// Handle page size change
sizeSelect.addEventListener("change", function() {
  currentPage = 1;
  loadClaims();
});

// Handle previous button
prevButton.addEventListener("click", function() {
  if (currentPage > 1) {
    currentPage = currentPage - 1;
    loadClaims();
  }
});

// Handle next button
nextButton.addEventListener("click", function() {
  if (currentPage < totalPages) {
    currentPage = currentPage + 1;
    loadClaims();
  }
});

// Only show the submit form if user is an EMPLOYEE
var role = getUserRole();
if (role !== "EMPLOYEE") {
  submitCard.classList.add("hidden");
} else {
  // Set up the submit form for employees
  var session = loadSession();
  document.getElementById("employeeLine").textContent = "Employee: " + session.name + " (#" + session.userId + ")";

  claimForm.addEventListener("submit", function(event) {
    event.preventDefault();

    // Get form values
    var amount = Number(document.getElementById("amount").value);
    var description = document.getElementById("description").value.trim();
    var claimLimit = CONFIG.CLAIM_LIMIT;

    // Check if amount is within limit
    if (amount > claimLimit) {
      showMessage("msg", "Amount is above configured limit.", true);
      return;
    }

    // Submit the claim
    callAPI("/claims", {
      method: "POST",
      body: JSON.stringify({
        amount: amount,
        description: description,
        employeeId: session.userId
      })
    }).then(function() {
      claimForm.reset();
      loadClaims();
    }).catch(function(error) {
      showMessage("msg", error.message, true);
      if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
        clearSession();
      }
    });
  });
}

// Load users first, then load claims
loadUsers().then(function() {
  loadClaims();
});
