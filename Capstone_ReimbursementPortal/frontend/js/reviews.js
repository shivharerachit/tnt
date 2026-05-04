// REVIEWS PAGE SCRIPT (ADMIN & MANAGER ONLY)
// Approve or reject pending claims

// Check if user is logged in and set up page
setupPage("reviews");

// Only admins and managers can access this page
let role = getUserRole();
if (role !== "ADMIN" && role !== "MANAGER") {
  window.location.href = "dashboard.html";
}

// Get HTML elements
let messageBox = document.getElementById("msg");
let pendingList = document.getElementById("pendingList");
let claimIdSelect = document.getElementById("claimId");
let reviewForm = document.getElementById("reviewForm");
let decisionSelect = document.getElementById("decision");
let commentInput = document.getElementById("comment");

// Load pending claims
function loadPendingClaims() {
  let path = "/claims/reviewer/paginated?page=0&size=30";

  callAPI(path).then(function(response) {
    let claimsData = extractData(response);
    let claims = claimsData.content || [];

    // Filter only SUBMITTED claims
    let pending = [];
    for (let i = 0; i < claims.length; i++) {
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
    let html = "";
    for (let i = 0; i < pending.length; i++) {
      let claim = pending[i];
      html = html + '<p>#' + claim.id + ' | Employee: ' + claim.employeeId + ' | ' + formatMoney(claim.amount) + '</p>';
    }
    pendingList.innerHTML = html;

    // Build options for claim dropdown
    let optionsHTML = "";
    for (let i = 0; i < pending.length; i++) {
      optionsHTML = optionsHTML + '<option value="' + pending[i].id + '">Claim #' + pending[i].id + '</option>';
    }
    claimIdSelect.innerHTML = optionsHTML;

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

  let claimId = claimIdSelect.value;
  let decision = decisionSelect.value;
  let comments = commentInput.value.trim();

  // Determine the API endpoint based on decision
  let endpoint;
  if (decision === "APPROVED") {
    endpoint = "/claims/" + claimId + "/approve";
  } else {
    endpoint = "/claims/" + claimId + "/reject";
  }

  // URL encode the comments
  let encodedComments = encodeURIComponent(comments);
  let fullPath = endpoint + "?comments=" + encodedComments;

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
