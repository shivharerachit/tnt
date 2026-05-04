// REVIEWS PAGE SCRIPT (ADMIN & MANAGER ONLY)
// Approve or reject pending claims

// Check if user is logged in and set up page
setupPage("reviews");

// Only admins and managers can access this page
var role = getUserRole();
if (role !== "ADMIN" && role !== "MANAGER") {
  window.location.href = "dashboard.html";
}

// Get HTML elements
var messageBox = document.getElementById("msg");
var pendingList = document.getElementById("pendingList");
var claimIdSelect = document.getElementById("claimId");
var reviewForm = document.getElementById("reviewForm");
var decisionSelect = document.getElementById("decision");
var commentInput = document.getElementById("comment");

// Load pending claims
function loadPendingClaims() {
  var path = "/claims/reviewer/paginated?page=0&size=30";

  callAPI(path).then(function(response) {
    var claimsData = extractData(response);
    var claims = claimsData.content || [];

    // Filter only SUBMITTED claims
    var pending = [];
    var i;
    for (i = 0; i < claims.length; i++) {
      if (claims[i].status === "SUBMITTED") {
        pending.push(claims[i]);
      }
    }

    // If no pending claims
    if (pending.length === 0) {
      pendingList.innerHTML = "<p>No pending claims.</p>";
      claimIdSelect.innerHTML = '<option value="">No pending claims</option>';
      return;
    }

    // Build HTML for pending list
    var html = "";
    var i;
    for (i = 0; i < pending.length; i++) {
      var claim = pending[i];
      html = html + '<p>#' + claim.id + ' | Employee: ' + claim.employeeId + ' | ' + formatMoney(claim.amount) + '</p>';
    }
    pendingList.innerHTML = html;

    // Build options for claim dropdown
    var optionsHTML = "";
    for (i = 0; i < pending.length; i++) {
      optionsHTML = optionsHTML + '<option value="' + pending[i].id + '">Claim #' + pending[i].id + '</option>';
    }
    claimIdSelect.innerHTML = optionsHTML;

    showMessage("msg", "Pending claims loaded.", false);
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
}

// Handle review form submission
reviewForm.addEventListener("submit", function(event) {
  event.preventDefault();

  var claimId = claimIdSelect.value;
  var decision = decisionSelect.value;
  var comments = commentInput.value.trim();

  // Determine the API endpoint based on decision
  var endpoint;
  if (decision === "APPROVED") {
    endpoint = "/claims/" + claimId + "/approve";
  } else {
    endpoint = "/claims/" + claimId + "/reject";
  }

  // URL encode the comments
  var encodedComments = encodeURIComponent(comments);
  var fullPath = endpoint + "?comments=" + encodedComments;

  callAPI(fullPath, {
    method: "PUT"
  }).then(function() {
    reviewForm.reset();
    loadPendingClaims();
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
});

// Load pending claims when page loads
loadPendingClaims();
