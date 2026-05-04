/**
 * CLAIMS PAGE (ExpenseEase)
 *
 * Flow:
 * 1. loadUsers() for name lookup in the table.
 * 2. loadClaims() calls the paginated API with page, size, sort, and optional status
 *    (all applied in the database — not only on the current page).
 * 3. View / Review open modals using GET /claims/{id} so rows work even off-page.
 */

setupPage("claims");

let messageBox = document.getElementById("msg");
let claimsTableBody = document.getElementById("claimsBody");
let pageInfoElement = document.getElementById("pageInfo");
let sizeSelect = document.getElementById("size");
let prevButton = document.getElementById("prev");
let nextButton = document.getElementById("next");
let claimForm = document.getElementById("claimForm");
let submitClaimBtn = document.getElementById("submitClaimBtn");
let filterStatusSelect = document.getElementById("filterStatus");

let allUsers = [];
let currentPage = 1;
let totalPages = 1;
/** Must match JPA field names on Claim (id, employeeId, title, amount, status, …) */
let sortColumn = "id";
let sortAsc = false;

function getClaimsPath() {
  let role = getUserRole();

  if (role === "ADMIN") {
    return "/claims/all/paginated";
  }
  if (role === "MANAGER") {
    return "/claims/reviewer/paginated";
  }
  return "/claims/my/paginated";
}

function getUsersPath() {
  let role = getUserRole();
  let session = loadSession();

  if (role === "ADMIN") {
    return "/users";
  }
  if (role === "MANAGER") {
    return "/users/manager/" + session.userId;
  }
  return null;
}

function getUserName(userId) {
  let session = loadSession();

  if (String(userId) === String(session.userId)) {
    return session.name;
  }

  let i;
  for (i = 0; i < allUsers.length; i++) {
    if (String(allUsers[i].id) === String(userId)) {
      return allUsers[i].name;
    }
  }

  return "#" + userId;
}

/**
 * Query string for Spring Data: page, size, sort=property,direction, optional status=ENUM
 */
function buildClaimsQueryString() {
  let pageSize = Number(sizeSelect.value);
  let parts = [];
  parts.push("page=" + encodeURIComponent(String(currentPage - 1)));
  parts.push("size=" + encodeURIComponent(String(pageSize)));
  parts.push(
    "sort=" +
      encodeURIComponent(sortColumn + "," + (sortAsc ? "asc" : "desc"))
  );
  /** Use claimStatus (not status) — avoids clashes with Spring sort/page machinery */
  let statusVal = "";
  if (filterStatusSelect) {
    statusVal = String(filterStatusSelect.value || "").trim();
  }
  if (statusVal) {
    parts.push("claimStatus=" + encodeURIComponent(statusVal));
  }
  return parts.join("&");
}

function buildActionButtonsHtml(claim, role) {
  let html = '<div class="claims-actions">';
  html +=
    '<button type="button" class="btn btn-secondary btn-sm" data-claim-id="' +
    claim.id +
    '" data-action="view">View</button>';

  let canReview =
    (role === "MANAGER" || role === "ADMIN") && claim.status === "SUBMITTED";
  if (canReview) {
    html +=
      '<button type="button" class="btn btn-review btn-sm" data-claim-id="' +
      claim.id +
      '" data-action="review">Review</button>';
  }

  if (role === "ADMIN") {
    html +=
      '<button type="button" class="btn btn-outline-danger btn-sm" data-claim-id="' +
      claim.id +
      '" data-action="delete">Delete</button>';
  }

  html += "</div>";
  return html;
}

function attachActionButtonListeners() {
  let buttons = claimsTableBody.querySelectorAll("button[data-action]");
  let i;
  for (i = 0; i < buttons.length; i++) {
    buttons[i].addEventListener("click", function () {
      let claimId = this.getAttribute("data-claim-id");
      let action = this.getAttribute("data-action");

      if (action === "view") {
        handleViewClaim(claimId);
      } else if (action === "review") {
        handleSubmitReview(claimId);
      } else if (action === "delete") {
        handleDeleteClaim(claimId);
      }
    });
  }
}

function displayClaims(claims) {
  if (claims.length === 0) {
    claimsTableBody.innerHTML =
      '<tr><td colspan="8">No claims found.</td></tr>';
    return;
  }

  let html = "";
  let i;
  let role = getUserRole();

  for (i = 0; i < claims.length; i++) {
    let claim = claims[i];
    let actionHtml = buildActionButtonsHtml(claim, role);

    html += "<tr>";
    html += "<td>" + claim.id + "</td>";
    html += "<td>" + getUserName(claim.employeeId) + "</td>";
    html += "<td>" + (claim.title || "-") + "</td>";
    html += "<td>" + formatMoney(claim.amount) + "</td>";
    html +=
      '<td><span class="badge ' +
      claim.status +
      '">' +
      claim.status +
      "</span></td>";
    html +=
      "<td>" +
      (claim.reviewerId ? getUserName(claim.reviewerId) : "-") +
      "</td>";
    html += "<td>" + (claim.comments || "-") + "</td>";
    html += "<td>" + actionHtml + "</td>";
    html += "</tr>";
  }

  claimsTableBody.innerHTML = html;
  attachActionButtonListeners();
}

function loadClaims() {
  let claimsPath = getClaimsPath();
  let path = claimsPath + "?" + buildClaimsQueryString();

  callAPI(path)
    .then(function (response) {
      let claimsData = extractData(response);

      if (claimsData) {
        totalPages = claimsData.totalPages || 1;
        currentPage = (claimsData.number || 0) + 1;
        pageInfoElement.textContent =
          "Page " + currentPage + " of " + totalPages;
        displayClaims(claimsData.content || []);
      } else {
        displayClaims([]);
      }
    })
    .catch(function (error) {
      showMessage("msg", error.message, true);
      if (
        error.message.indexOf("User not found") !== -1 ||
        error.message.indexOf("Invalid") !== -1
      ) {
        clearSession();
      }
    });
}

function handleDeleteClaim(claimId) {
  let confirmed = window.confirm("Delete claim #" + claimId + "?");
  if (!confirmed) {
    return;
  }

  callAPI("/claims/" + claimId, {
    method: "DELETE",
  })
    .then(function () {
      showMessage("msg", "Claim deleted.", false);
      loadClaims();
    })
    .catch(function (error) {
      showMessage("msg", error.message, true);
      if (
        error.message.indexOf("User not found") !== -1 ||
        error.message.indexOf("Invalid") !== -1
      ) {
        clearSession();
      }
    });
}

function renderClaimDetailHtml(claim) {
  return (
    '<div class="claim-details-grid">' +
    "<div><strong>Claim ID:</strong> " +
    claim.id +
    "</div>" +
    "<div><strong>Employee:</strong> " +
    getUserName(claim.employeeId) +
    "</div>" +
    "<div><strong>Title:</strong> " +
    (claim.title || "-") +
    "</div>" +
    "<div><strong>Description:</strong> " +
    (claim.description || "-") +
    "</div>" +
    "<div><strong>Amount:</strong> " +
    formatMoney(claim.amount) +
    "</div>" +
    '<div><strong>Status:</strong> <span class="badge ' +
    claim.status +
    '">' +
    claim.status +
    "</span></div>" +
    "<div><strong>Date:</strong> " +
    (claim.date ? new Date(claim.date).toLocaleDateString() : "-") +
    "</div>" +
    "<div><strong>Reviewer:</strong> " +
    (claim.reviewerId ? getUserName(claim.reviewerId) : "-") +
    "</div>" +
    "<div><strong>Comments:</strong> " +
    (claim.comments || "-") +
    "</div>" +
    "</div>"
  );
}

function handleViewClaim(claimId) {
  callAPI("/claims/" + encodeURIComponent(claimId))
    .then(function (response) {
      let claim = extractData(response);
      if (!claim) {
        showMessage("msg", "Claim not found.", true);
        return;
      }
      document.getElementById("claimDetails").innerHTML =
        renderClaimDetailHtml(claim);
      openModal("viewClaimModal");
    })
    .catch(function (error) {
      showMessage("msg", error.message, true);
      if (
        error.message.indexOf("User not found") !== -1 ||
        error.message.indexOf("Invalid") !== -1
      ) {
        clearSession();
      }
    });
}

function handleSubmitReview(claimId) {
  callAPI("/claims/" + encodeURIComponent(claimId))
    .then(function (response) {
      let claim = extractData(response);
      if (!claim) {
        showMessage("msg", "Claim not found.", true);
        return;
      }
      document.getElementById("reviewClaimId").value = claimId;
      document.getElementById("reviewClaimInfo").textContent =
        "Claim #" +
        claimId +
        " — " +
        (claim.title || "(no title)") +
        " — " +
        formatMoney(claim.amount);
      document.getElementById("reviewForm").reset();
      document.getElementById("reviewClaimId").value = claimId;
      openModal("submitReviewModal");
    })
    .catch(function (error) {
      showMessage("msg", error.message, true);
      if (
        error.message.indexOf("User not found") !== -1 ||
        error.message.indexOf("Invalid") !== -1
      ) {
        clearSession();
      }
    });
}

function submitReview() {
  document.getElementById("reviewForm").dispatchEvent(new Event("submit"));
}

function submitClaim() {
  claimForm.dispatchEvent(new Event("submit"));
}

function loadUsers() {
  let usersPath = getUsersPath();
  let session = loadSession();

  if (!usersPath) {
    allUsers = [
      {
        id: session.userId,
        name: session.name,
      },
    ];
    return Promise.resolve();
  }

  return callAPI(usersPath).then(function (response) {
    allUsers = extractData(response) || [];
  });
}

sizeSelect.addEventListener("change", function () {
  currentPage = 1;
  loadClaims();
});

prevButton.addEventListener("click", function () {
  if (currentPage > 1) {
    currentPage = currentPage - 1;
    loadClaims();
  }
});

nextButton.addEventListener("click", function () {
  if (currentPage < totalPages) {
    currentPage = currentPage + 1;
    loadClaims();
  }
});

if (filterStatusSelect) {
  filterStatusSelect.addEventListener("change", function () {
    currentPage = 1;
    loadClaims();
  });
}

let sortableHeaders = document.querySelectorAll("th.sortable");
for (let h = 0; h < sortableHeaders.length; h++) {
  sortableHeaders[h].addEventListener("click", function () {
    let col = this.getAttribute("data-sort");
    if (sortColumn === col) {
      sortAsc = !sortAsc;
    } else {
      sortColumn = col;
      sortAsc = true;
    }
    currentPage = 1;
    loadClaims();
  });
}

let role = getUserRole();
if (role === "EMPLOYEE") {
  submitClaimBtn.style.display = "block";

  let session = loadSession();
  document.getElementById("employeeLine").textContent =
    "Employee: " + session.name + " (#" + session.userId + ")";

  submitClaimBtn.addEventListener("click", function () {
    openModal("submitClaimModal");
  });

  claimForm.addEventListener("submit", function (event) {
    event.preventDefault();

    let title = document.getElementById("title").value.trim();
    let description = document.getElementById("description").value.trim();
    let amount = Number(document.getElementById("amount").value);
    let dateInput = document.getElementById("date").value;
    let claimLimit = CONFIG.CLAIM_LIMIT;

    if (!title || title.length < 3 || title.length > 100) {
      showMessage("msg", "Title must be between 3 and 100 characters.", true);
      return;
    }

    if (!description || description.length < 5 || description.length > 500) {
      showMessage(
        "msg",
        "Description must be between 5 and 500 characters.",
        true
      );
      return;
    }

    if (amount <= 0) {
      showMessage("msg", "Amount must be greater than 0.", true);
      return;
    }

    if (amount > claimLimit) {
      showMessage(
        "msg",
        "Amount is above configured limit (₹" + claimLimit + ").",
        true
      );
      return;
    }

    if (dateInput) {
      let selectedDate = new Date(dateInput);
      let today = new Date();
      today.setHours(0, 0, 0, 0);
      if (selectedDate > today) {
        showMessage("msg", "Date cannot be in the future.", true);
        return;
      }
    }

    let requestBody = {
      title: title,
      description: description,
      amount: amount,
      employeeId: session.userId,
    };

    if (dateInput) {
      requestBody.date = dateInput;
    }

    callAPI("/claims", {
      method: "POST",
      body: JSON.stringify(requestBody),
    })
      .then(function () {
        claimForm.reset();
        closeModal("submitClaimModal");
        currentPage = 1;
        loadClaims();
        showMessage("msg", "Claim submitted successfully.", false);
      })
      .catch(function (error) {
        showMessage("msg", error.message, true);
        if (
          error.message.indexOf("User not found") !== -1 ||
          error.message.indexOf("Invalid") !== -1
        ) {
          clearSession();
        }
      });
  });
}

let reviewForm = document.getElementById("reviewForm");
if (reviewForm) {
  reviewForm.addEventListener("submit", function (event) {
    event.preventDefault();

    let claimId = document.getElementById("reviewClaimId").value;
    let decision = document.getElementById("reviewDecision").value;
    let comments = document.getElementById("reviewComments").value.trim();

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

    let endpoint =
      decision === "APPROVED"
        ? "/claims/" + claimId + "/approve"
        : "/claims/" + claimId + "/reject";

    callAPI(endpoint + "?comments=" + encodeURIComponent(comments), {
      method: "PUT",
    })
      .then(function () {
        reviewForm.reset();
        closeModal("submitReviewModal");
        loadClaims();
        showMessage("msg", "Review submitted successfully.", false);
      })
      .catch(function (error) {
        showMessage("msg", error.message, true);
        if (
          error.message.indexOf("User not found") !== -1 ||
          error.message.indexOf("Invalid") !== -1
        ) {
          clearSession();
        }
      });
  });
}

loadUsers().then(function () {
  loadClaims();
});
