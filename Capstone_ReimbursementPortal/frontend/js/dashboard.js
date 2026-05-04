/**
 * DASHBOARD — summary stats and recent claims.
 *
 * Uses non-paginated claim endpoints so totals match the whole dataset (not just page 1).
 * ADMIN: GET /users + GET /claims/all
 * MANAGER: GET /users/manager/{id} + GET /claims/reviewer
 * EMPLOYEE: GET /claims/user-claims (user count is always 1)
 */

setupPage("dashboard");
redirectIfNotLoggedIn();

let usersCountElement = document.getElementById("usersCount");
let claimsCountElement = document.getElementById("claimsCount");
let pendingCountElement = document.getElementById("pendingCount");
let approvedAmountElement = document.getElementById("approvedAmount");
let activityListElement = document.getElementById("activityList");

function getDashboardPaths() {
  let role = getUserRole();
  let session = loadSession();
  let userId = session.userId;

  if (role === "ADMIN") {
    return {
      usersPath: "/users",
      claimsPath: "/claims/all",
      scope: "admin",
    };
  }

  if (role === "MANAGER") {
    return {
      usersPath: "/users/manager/" + userId,
      claimsPath: "/claims/reviewer",
      scope: "manager",
    };
  }

  return {
    usersPath: null,
    claimsPath: "/claims/user-claims",
    scope: "employee",
  };
}

function applyRoleCopy(scope) {
  let greetingEl = document.getElementById("dashboardGreeting");
  let subEl = document.getElementById("dashboardWelcomeSub");
  let session = loadSession();

  greetingEl.textContent = "Welcome back, " + (session.name || "there");

  if (scope === "admin") {
    subEl.textContent =
      "Organization-wide view — all users and claims in the system.";
    document.getElementById("statUsersHeading").textContent = "Users";
    document.getElementById("statUsersFoot").textContent = "Accounts you can administer";
    document.getElementById("statClaimsFoot").textContent = "Total expense claims";
    document.getElementById("statPendingFoot").textContent = "Awaiting a decision";
    document.getElementById("statApprovedFoot").textContent = "Sum of approved claims";
    document.getElementById("statClaimsHeading").textContent = "Claims";
  } else if (scope === "manager") {
    subEl.textContent =
      "Your team and claims routed to you for review.";
    document.getElementById("statUsersHeading").textContent = "Team members";
    document.getElementById("statUsersFoot").textContent = "Users reporting to you";
    document.getElementById("statClaimsFoot").textContent = "Claims in your review queue";
    document.getElementById("statPendingFoot").textContent = "Still need your review";
    document.getElementById("statApprovedFoot").textContent = "Approved in your queue";
    document.getElementById("statClaimsHeading").textContent = "Assigned claims";
  } else {
    subEl.textContent = "Track your submissions and reimbursement status.";
    document.getElementById("statUsersHeading").textContent = "You";
    document.getElementById("statUsersFoot").textContent = "Logged-in account";
    document.getElementById("statClaimsFoot").textContent = "Claims you submitted";
    document.getElementById("statPendingFoot").textContent = "Waiting on reviewer";
    document.getElementById("statApprovedFoot").textContent = "Your approved total";
    document.getElementById("statClaimsHeading").textContent = "My claims";
  }
}

/**
 * Normalize API payload to a plain array of claims.
 */
function claimsArrayFromResponse(raw) {
  if (!raw) return [];
  if (Array.isArray(raw)) return raw;
  if (Array.isArray(raw.content)) return raw.content;
  return [];
}

function sortClaimsRecentFirst(list) {
  let copy = list.slice();
  copy.sort(function (a, b) {
    return Number(b.id || 0) - Number(a.id || 0);
  });
  return copy;
}

function sumApprovedAmount(list) {
  let total = 0;
  let i;
  for (i = 0; i < list.length; i++) {
    if (list[i].status === "APPROVED") {
      total += Number(list[i].amount) || 0;
    }
  }
  return total;
}

function countPending(list) {
  let n = 0;
  let i;
  for (i = 0; i < list.length; i++) {
    if (list[i].status === "SUBMITTED") {
      n++;
    }
  }
  return n;
}

function renderActivityList(claims) {
  if (!claims.length) {
    activityListElement.innerHTML =
      '<p class="dashboard-activity-empty small">No claims to show yet.</p>';
    return;
  }

  let recent = sortClaimsRecentFirst(claims).slice(0, 6);
  let html = '<ul class="dashboard-activity-ul">';
  let i;
  for (i = 0; i < recent.length; i++) {
    let c = recent[i];
    let title = c.title || "(No title)";
    html += '<li class="dashboard-activity-item">';
    html += '<div class="dashboard-activity-main">';
    html +=
      '<span class="dashboard-activity-title">' +
      title +
      '</span>';
    html +=
      '<span class="badge ' +
      (c.status || "") +
      '">' +
      (c.status || "-") +
      "</span>";
    html += "</div>";
    html += '<div class="dashboard-activity-meta small">';
    html += "Claim #" + (c.id != null ? c.id : "-");
    html += " · " + formatMoney(c.amount || 0);
    if (c.date) {
      html += " · " + c.date;
    }
    html += "</div>";
    html += "</li>";
  }
  html += "</ul>";
  activityListElement.innerHTML = html;
}

function loadDashboard() {
  let paths = getDashboardPaths();
  applyRoleCopy(paths.scope);

  let usersPromise;
  if (!paths.usersPath) {
    usersPromise = Promise.resolve(1);
  } else {
    usersPromise = callAPI(paths.usersPath).then(function (response) {
      let users = extractData(response);
      if (!Array.isArray(users)) return 0;
      return users.length;
    });
  }

  let claimsPromise = callAPI(paths.claimsPath).then(function (response) {
    return extractData(response);
  });

  Promise.all([usersPromise, claimsPromise])
    .then(function (results) {
      let userCount = results[0];
      let claimsRaw = results[1];
      let claimsList = claimsArrayFromResponse(claimsRaw);

      usersCountElement.textContent = String(userCount);
      claimsCountElement.textContent = String(claimsList.length);
      pendingCountElement.textContent = String(countPending(claimsList));
      approvedAmountElement.textContent = formatMoney(
        sumApprovedAmount(claimsList)
      );

      renderActivityList(claimsList);
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

loadDashboard();
