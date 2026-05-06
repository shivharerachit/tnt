/**
 * DASHBOARD — summary stats and recent claims.
 *
 * Claim stats use paginated APIs + totalElements
 * by the first page. Activity uses one small page.
 * ADMIN: GET /users + GET /claims/all/paginated
 * MANAGER: GET /users/manager/{id} + GET /claims/reviewer/paginated
 * EMPLOYEE: GET /claims/my/paginated
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

  if (role === ROLE.ADMIN) {
    return {
      usersPath: "/users",
      claimsPaginatedPath: "/claims/all/paginated",
      scope: DASHBOARD_SCOPE.ADMIN,
    };
  }

  if (role === ROLE.MANAGER) {
    return {
      usersPath: "/users/manager/" + userId,
      claimsPaginatedPath: "/claims/reviewer/paginated",
      scope: DASHBOARD_SCOPE.MANAGER,
    };
  }

  return {
    usersPath: null,
    claimsPaginatedPath: "/claims/my/paginated",
    scope: DASHBOARD_SCOPE.EMPLOYEE,
  };
}

function applyRoleCopy(scope) {
  let greetingEl = document.getElementById("dashboardGreeting");
  let subEl = document.getElementById("dashboardWelcomeSub");
  let session = loadSession();

  greetingEl.textContent = "Welcome back, " + (session.name || "there");

  if (scope === DASHBOARD_SCOPE.ADMIN) {
    subEl.textContent =
      "Organization-wide view — all users and claims in the system.";
    document.getElementById("statUsersHeading").textContent = "Users";
    document.getElementById("statUsersFoot").textContent = "Accounts you can administer";
    document.getElementById("statClaimsFoot").textContent = "Total expense claims";
    document.getElementById("statPendingFoot").textContent = "Awaiting a decision";
    document.getElementById("statApprovedFoot").textContent = "Sum of approved claims";
    document.getElementById("statClaimsHeading").textContent = "Claims";
  } else if (scope === DASHBOARD_SCOPE.MANAGER) {
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

const DASHBOARD_ACTIVITY_SIZE = 6;
const DASHBOARD_APPROVED_PAGE_SIZE = 100;

function sortClaimsRecentFirst(list) {
  let copy = list.slice();
  copy.sort(function (a, b) {
    return Number(b.id || 0) - Number(a.id || 0);
  });
  return copy;
}

function claimsPageQuery(params) {
  let page = params.page != null ? params.page : 0;
  let size = params.size != null ? params.size : 10;
  let parts = [];
  parts.push("page=" + encodeURIComponent(String(page)));
  parts.push("size=" + encodeURIComponent(String(size)));
  parts.push("sort=" + encodeURIComponent("id,desc"));
  if (params.claimStatus) {
    parts.push(
      "claimStatus=" + encodeURIComponent(String(params.claimStatus))
    );
  }
  return parts.join("&");
}

/**
 * Total rows for this query (Spring page) or list length (legacy non-paged body).
 */
function totalElementsFromClaimsPayload(data) {
  if (
    data &&
    typeof data === "object" &&
    !Array.isArray(data) &&
    typeof data.totalElements === "number" &&
    !Number.isNaN(data.totalElements)
  ) {
    return data.totalElements;
  }
  return claimsArrayFromResponse(data).length;
}

/**
 * Sum approved amounts by walking APPROVED pages (server-filtered).
 */
function sumApprovedAmountPaged(basePath) {
  let sum = 0;
  let page = 0;

  function fetchNext() {
    let qs = claimsPageQuery({
      page: page,
      size: DASHBOARD_APPROVED_PAGE_SIZE,
      claimStatus: CLAIM_STATUS.APPROVED,
    });
    return callAPI(basePath + "?" + qs)
      .then(extractData)
      .then(function (data) {
        let list = claimsArrayFromResponse(data);
        let i;
        for (i = 0; i < list.length; i++) {
          sum += Number(list[i].amount) || 0;
        }
        if (Array.isArray(data)) {
          return sum;
        }
        let totalPages =
          data && typeof data.totalPages === "number" && !Number.isNaN(data.totalPages)
            ? data.totalPages
            : 1;
        page++;
        if (list.length === 0 || page >= totalPages) {
          return sum;
        }
        return fetchNext();
      });
  }

  return fetchNext();
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

  let base = paths.claimsPaginatedPath;

  let totalClaimsPromise = callAPI(
    base + "?" + claimsPageQuery({ page: 0, size: 1 })
  )
    .then(extractData)
    .then(totalElementsFromClaimsPayload);

  let pendingTotalPromise = callAPI(
    base +
      "?" +
      claimsPageQuery({
        page: 0,
        size: 1,
        claimStatus: CLAIM_STATUS.SUBMITTED,
      })
  )
    .then(extractData)
    .then(totalElementsFromClaimsPayload);

  let activityPromise = callAPI(
    base +
      "?" +
      claimsPageQuery({ page: 0, size: DASHBOARD_ACTIVITY_SIZE })
  ).then(extractData);

  let approvedSumPromise = sumApprovedAmountPaged(base);

  Promise.all([
    usersPromise,
    totalClaimsPromise,
    pendingTotalPromise,
    approvedSumPromise,
    activityPromise,
  ])
    .then(function (results) {
      let userCount = results[0];
      let totalClaims = results[1];
      let pendingTotal = results[2];
      let approvedSum = results[3];
      let activityRaw = results[4];
      let activityList = claimsArrayFromResponse(activityRaw);

      usersCountElement.textContent = String(userCount);
      claimsCountElement.textContent = String(totalClaims);
      pendingCountElement.textContent = String(pendingTotal);
      approvedAmountElement.textContent = formatMoney(approvedSum);

      renderActivityList(activityList);
    })
    .catch(handleAPIError);
}

loadDashboard();
