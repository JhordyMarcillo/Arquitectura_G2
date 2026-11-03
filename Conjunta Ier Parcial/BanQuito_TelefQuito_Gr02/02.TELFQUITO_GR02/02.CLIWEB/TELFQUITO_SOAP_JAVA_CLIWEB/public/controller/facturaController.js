import { showModal } from "./modal.js";

document.addEventListener("DOMContentLoaded", () => {
  const urlParams = new URLSearchParams(window.location.search);
  const cedula = urlParams.get("cedula");

  if (cedula) {
    loadFacturaData(cedula);
  }
});

async function loadFacturaData(cedula) {
  try {
    const response = await fetch("/obtenerFactura", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ cedula }),
    });

    const result = await response.json();
    console.log(result);
    if (result.success && result.result.length > 0) {
      const factura = result.result[result.result.length - 1]; // Obtener la última factura
      document.getElementById("codCompra").textContent = factura.codCompra;
      document.getElementById("descuento").textContent = factura.descuento;
      document.getElementById("fecha").textContent = factura.fecha;
      document.getElementById("formaPago").textContent = factura.formaPago;
      document.getElementById("marcaTelefono").textContent =
        factura.marcaTelefono;
      document.getElementById("nombreCliente").textContent =
        factura.nombreCliente;
      document.getElementById("nombreTelefono").textContent =
        factura.nombreTelefono;
      document.getElementById("preciofinal").textContent = factura.preciofinal;
      document.getElementById("cedulaCliente").textContent = cedula;
    } else {
      showModal("Error", "Fallo en recuperar los datos de la factura.");
    }
  } catch (error) {
    console.error("Load factura data error:", error);
    showModal(
      "Error",
      "Un error ha ocurrido durante la carga de los datos de la factura. Por favor, revise la conexión."
    );
  }
}
