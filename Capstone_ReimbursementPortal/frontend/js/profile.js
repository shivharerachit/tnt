// PROFILE PAGE — view account details (read-only) and change password only

setupPage("profile");

redirectIfNotLoggedIn();

let passwordForm = document.getElementById("passwordForm");

function loadProfileData() {
  let session = loadSession();

  if (!session) {
    showMessage("msg", "Session not found. Please login again.", true);
    return;
  }

  document.getElementById("infoUserId").textContent = session.userId || "-";
  document.getElementById("infoName").textContent = session.name || "-";
  document.getElementById("infoEmail").textContent = session.email || "-";
  document.getElementById("infoRole").textContent = session.role || "-";
  document.getElementById("infoDate").textContent = new Date().toLocaleDateString();
}

if (passwordForm) {
  passwordForm.addEventListener("submit", function (event) {
    event.preventDefault();

    let currentPassword = document.getElementById("currentPassword").value;
    let newPassword = document.getElementById("newPassword").value;
    let confirmPassword = document.getElementById("confirmPassword").value;

    if (!currentPassword) {
      showMessage("msg", "Current password is required.", true);
      return;
    }

    let newPwError = validatePassword(newPassword);
    if (newPwError) {
      showMessage("msg", newPwError, true);
      return;
    }

    if (newPassword !== confirmPassword) {
      showMessage("msg", "New passwords do not match.", true);
      return;
    }

    if (currentPassword === newPassword) {
      showMessage(
        "msg",
        "New password must be different from current password.",
        true
      );
      return;
    }

    let session = loadSession();
    callAPI("/users/" + session.userId + "/change-password", {
      method: "PUT",
      body: JSON.stringify({
        currentPassword: currentPassword,
        newPassword: newPassword,
      }),
    })
      .then(function () {
        passwordForm.reset();
        showMessage("msg", "Password changed successfully.", false);
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

loadProfileData();
