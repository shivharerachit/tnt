// REVIEWS PAGE SCRIPT (ADMIN & MANAGER ONLY)
// Approve or reject pending claims

// Check if user is logged in and set up page
setupPage("reviews");

// Only admins and managers can access this page
let role = getUserRole();
if (role !== ROLE.ADMIN && role !== ROLE.MANAGER) {
  window.location.href = "dashboard.html";
}

// Get HTML elements
let messageBox = document.getElementById("msg");
let pendingList = document.getElementById("pendingList");
let claimIdSelect = document.getElementById("claimId");
let reviewForm = document.getElementById("reviewForm");
let decisionSelect = document.getElementById("decision");
let commentInput = document.getElementById("comment");

const REVIEWS_PAGE_SIZE = 100;
let isLoadingPendingClaims = false;
let isSubmittingReview = false;

function showLoadingMessage(text) {
  if (!messageBox) return;
  messageBox.textContent = text || "Loading...";
  messageBox.classList.remove("hidden");
  messageBox.classList.remove("error");
}

function hideLoadingMessage() {
  if (!messageBox) return;
  messageBox.classList.add("hidden");
}

function setReviewFormDisabled(disabled) {
  if (!reviewForm) return;
  let controls = reviewForm.querySelectorAll("button, input, select, textarea");
  for (let i = 0; i < controls.length; i++) {
    controls[i].disabled = !!disabled;
  }
}

function reviewerSubmittedQuery(page) {
  let parts = [
    "page=" + encodeURIComponent(String(page)),
    "size=" + encodeURIComponent(String(REVIEWS_PAGE_SIZE)),
    "sort=" + encodeURIComponent("id,desc"),
    "claimStatus=" + encodeURIComponent(CLAIM_STATUS.SUBMITTED),
  ];
  return "/claims/reviewer/paginated?" + parts.join("&");
}

function pageContentFromClaimsPayload(data) {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  if (Array.isArray(data.content)) return data.content;
  return [];
}

/**
 * Load every SUBMITTED claim in the reviewer queue (all pages).
 */
function fetchAllPendingReviewerClaims() {
  let merged = [];
  let page = 0;

  function fetchNext() {
    return callAPI(reviewerSubmittedQuery(page))
      .then(extractData)
      .then(function (data) {
        let chunk = pageContentFromClaimsPayload(data);
        let i;
        for (i = 0; i < chunk.length; i++) {
          merged.push(chunk[i]);
        }
        if (Array.isArray(data)) {
          return merged;
        }
        let totalPages =
          data && typeof data.totalPages === "number" && !Number.isNaN(data.totalPages)
            ? data.totalPages
            : 1;
        page++;
        if (chunk.length === 0 || page >= totalPages) {
          return merged;
        }
        return fetchNext();
      });
  }

  return fetchNext();
}

function renderPendingUI(pending) {
  if (pending.length === 0) {
    pendingList.innerHTML = "<p>No pending claims.</p>";
    claimIdSelect.innerHTML = '<option value="">No pending claims</option>';
    return;
  }

  let html = "";
  let optionsHTML = "";
  for (let i = 0; i < pending.length; i++) {
    let claim = pending[i];
    html =
      html +
      "<p>#" +
      claim.id +
      " | Employee: " +
      claim.employeeId +
      " | " +
      formatMoney(claim.amount) +
      "</p>";
    optionsHTML =
      optionsHTML +
      '<option value="' +
      claim.id +
      '">Claim #' +
      claim.id +
      "</option>";
  }
  pendingList.innerHTML = html;
  claimIdSelect.innerHTML = optionsHTML;
}

// Load pending claims
function loadPendingClaims() {
  if (isLoadingPendingClaims) return;
  isLoadingPendingClaims = true;
  showLoadingMessage("Loading pending claims...");

  fetchAllPendingReviewerClaims()
    .then(function (rows) {
      let pending = rows.filter(function (c) {
        return c.status === CLAIM_STATUS.SUBMITTED;
      });
      renderPendingUI(pending);
      hideLoadingMessage();
    })
    .catch(handleAPIError)
    .finally(function () {
      isLoadingPendingClaims = false;
    });
}

// Handle review form submission
reviewForm.addEventListener("submit", function(event) {
  event.preventDefault();
  if (isSubmittingReview) return;

  let claimId = claimIdSelect.value;
  let decision = decisionSelect.value;
  let comments = commentInput.value.trim();

  if (!claimId) {
    showMessage("msg", "Please select a claim.", true);
    return;
  }

  if (!decision) {
    showMessage(
      "msg",
      "Please select a decision (Approve or Reject).",
      true
    );
    return;
  }

  if (!comments) {
    showMessage("msg", "Comments are required.", true);
    return;
  }

  // Determine the API endpoint based on decision
  let endpoint;
  if (decision === CLAIM_STATUS.APPROVED) {
    endpoint = "/claims/" + claimId + "/approve";
  } else {
    endpoint = "/claims/" + claimId + "/reject";
  }

  // URL encode the comments
  let encodedComments = encodeURIComponent(comments);
  let fullPath = endpoint + "?comments=" + encodedComments;
  isSubmittingReview = true;
  setReviewFormDisabled(true);
  showLoadingMessage("Submitting review...");

  callAPI(fullPath, {
    method: "PUT"
  }).then(function() {
    reviewForm.reset();
    loadPendingClaims();
  }).catch(handleAPIError)
    .finally(function () {
      isSubmittingReview = false;
      setReviewFormDisabled(false);
      hideLoadingMessage();
    });
});

// Load pending claims when page loads
loadPendingClaims();
