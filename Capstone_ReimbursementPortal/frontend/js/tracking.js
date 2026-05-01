// TRACKING PAGE SCRIPT
// Track claim status and resubmit rejected claims

// Check if user is logged in and set up page
setupPage("tracking");

// Get HTML elements
var messageBox = document.getElementById("msg");
var resultSection = document.getElementById("result");
var resubmitCard = document.getElementById("resubmitCard");
var searchButton = document.getElementById("searchBtn");
var searchInput = document.getElementById("searchId");
var resubmitForm = document.getElementById("resubmitForm");
var resubmitIdInput = document.getElementById("resubmitId");
var resubmitAmountInput = document.getElementById("resubmitAmount");
var resubmitDescriptionInput = document.getElementById("resubmitDescription");

// Display claim details
function displayClaim(claim) {
  // If no claim found
  if (!claim) {
    resultSection.innerHTML = "<p>No claim found.</p>";
    resubmitCard.classList.add("hidden");
    return;
  }

  // Display claim info
  var html = "" +
    '<p><strong>Claim #:</strong> ' + claim.id + '</p>' +
    '<p><strong>Employee ID:</strong> ' + claim.employeeId + '</p>' +
    '<p><strong>Amount:</strong> ' + formatMoney(claim.amount) + '</p>' +
    '<p><strong>Status:</strong> <span class="badge ' + claim.status + '">' + claim.status + '</span></p>' +
    '<p><strong>Reviewer ID:</strong> ' + claim.reviewerId + '</p>' +
    '<p><strong>Comments:</strong> ' + (claim.comments || '-') + '</p>';

  resultSection.innerHTML = html;

  // Check if this is a rejected claim and if user can resubmit it
  var session = loadSession();
  var isUsersClaim = String(claim.employeeId) === String(session.userId);
  var isRejected = claim.status === "REJECTED";
  var isEmployee = getUserRole() === "EMPLOYEE";

  if (isEmployee && isUsersClaim && isRejected) {
    // Show resubmit form
    resubmitCard.classList.remove("hidden");
    resubmitIdInput.value = claim.id;
    resubmitAmountInput.value = claim.amount;
    resubmitDescriptionInput.value = "";
  } else {
    // Hide resubmit form
    resubmitCard.classList.add("hidden");
  }
}

// Search for a claim by ID
searchButton.addEventListener("click", function() {
  var claimId = Number(searchInput.value);

  if (!claimId) {
    showMessage("msg", "Enter claim id.", true);
    return;
  }

  callAPI("/claims/" + claimId).then(function(response) {
    var claim = extractData(response);
    displayClaim(claim);
    showMessage("msg", "Claim loaded.", false);
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    displayClaim(null);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
});

// Handle resubmit form submission
resubmitForm.addEventListener("submit", function(event) {
  event.preventDefault();

  var claimId = resubmitIdInput.value;
  var amount = Number(resubmitAmountInput.value);
  var description = resubmitDescriptionInput.value.trim();
  var claimLimit = CONFIG.CLAIM_LIMIT;
  var session = loadSession();

  // Check if amount is within limit
  if (amount > claimLimit) {
    showMessage("msg", "Amount is above configured limit.", true);
    return;
  }

  // Submit the resubmitted claim
  callAPI("/claims/" + claimId, {
    method: "PUT",
    body: JSON.stringify({
      amount: amount,
      description: description,
      employeeId: session.userId
    })
  }).then(function() {
    resubmitForm.reset();
    resubmitCard.classList.add("hidden");
    showMessage("msg", "Claim resubmitted.", false);
  }).catch(function(error) {
    showMessage("msg", error.message, true);
    if (error.message.indexOf("User not found") !== -1 || error.message.indexOf("Invalid") !== -1) {
      clearSession();
    }
  });
});
