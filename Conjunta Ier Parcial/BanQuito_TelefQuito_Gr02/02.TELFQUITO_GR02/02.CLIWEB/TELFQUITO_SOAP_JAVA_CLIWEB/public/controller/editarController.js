import { showModal } from "./modal.js";

document.addEventListener("DOMContentLoaded", () => {
  const urlParams = new URLSearchParams(window.location.search);
  const telefonoId = urlParams.get("id");

  if (telefonoId) {
    loadTelefonoData(telefonoId);
  }

  document
    .getElementById("telefonoForm")
    .addEventListener("submit", async (e) => {
      e.preventDefault();

      const codTelefono = parseInt(
        document.getElementById("telefonoId").value,
        10
      );
      const nombre = document.getElementById("nombre").value;
      const precio = parseFloat(document.getElementById("precio").value);
      const marca = document.getElementById("marca").value;
      const disponible =
        document.getElementById("disponible").value === "true" ? 1 : 0;

      const telefono = {
        codTelefono,
        nombre,
        precio,
        marca,
        disponible,
      };

      try {
        const response = await fetch("/updateTelefono", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ telefono }),
        });

        const result = await response.json();
        console.log(result);
        if (result.success) {
          showModal("Éxito", "Teléfono actualizado exitosamente.", () => {
            window.location.href = "/telefonos";
          });
          // window.location.href = "/telefonos";
        } else {
          showModal("Error", "Fallo al actualizar el teléfono.");
        }
      } catch (error) {
        console.error("Update telefono error:", error);
        showModal(
          "Error",
          "Un error ha ocurrido durante la actualización del teléfono. Por favor, revise la conexión."
        );
      }
    });
});

async function loadTelefonoData(id) {
  try {
    const response = await fetch("/getTelefonoById", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ id }),
    });

    const result = await response.json();

    if (result.success) {
      const telefono = result.result;
      document.getElementById("telefonoId").value = telefono.codTelefono;
      document.getElementById("nombre").value = telefono.nombre;
      document.getElementById("precio").value = telefono.precio;
      document.getElementById("marca").value = telefono.marca;
      document.getElementById("disponible").value = telefono.disponible
        ? "true"
        : "false";
    } else {
      showModal("Error", "Fallo en recuperar los datos del teléfono.");
    }
  } catch (error) {
    console.error("Load telefono data error:", error);
    showModal(
      "Error",
      "Un error ha ocurrido durante la carga de los datos del teléfono. Por favor, revise la conexión."
    );
  }
}
