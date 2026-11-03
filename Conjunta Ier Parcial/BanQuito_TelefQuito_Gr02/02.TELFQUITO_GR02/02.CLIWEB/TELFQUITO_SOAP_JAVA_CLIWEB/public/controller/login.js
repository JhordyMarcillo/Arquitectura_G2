import { showModal } from "/controller/modal.js";

document.getElementById("loginForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const password = document.getElementById("password").value;

  try {
    const response = await fetch("/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });

    const result = await response.json();

    if (result.success) {
      window.location.href = "/telefonos";
    } else {
      showModal("Login Fallido", "Por favor, intente de nuevo.");
    }
  } catch (error) {
    console.error("Login error:", error);
    showModal(
      "Error",
      "Ocurrió un error durante el login. Por favor, revise la conexión."
    );
  }
});
