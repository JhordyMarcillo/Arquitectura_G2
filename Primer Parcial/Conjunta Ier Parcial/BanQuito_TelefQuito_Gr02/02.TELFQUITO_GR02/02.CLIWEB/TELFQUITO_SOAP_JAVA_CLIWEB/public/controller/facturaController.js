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
      // Ultima factura: puede tener hasta 3 lineas (mismo codCompra)
      const filas = result.result;
      const ultima = filas[filas.length - 1].codCompra;
      const lineas = filas.filter((f) => f.codCompra === ultima);
      const factura = lineas[0];
      const suma = (campo) => lineas.reduce((a, f) => a + Number(f[campo] || 0), 0);
      const dinero = (v) => v.toFixed(2);
      document.getElementById("codCompra").textContent = factura.codCompra;
      document.getElementById("descuento").textContent = dinero(suma("descuento"));
      document.getElementById("fecha").textContent = factura.fecha;
      document.getElementById("formaPago").textContent = factura.formaPago;
      document.getElementById("marcaTelefono").textContent = lineas
        .map((f) => f.marcaTelefono)
        .join(", ");
      document.getElementById("nombreCliente").textContent = factura.nombreCliente;
      document.getElementById("nombreTelefono").textContent = lineas
        .map((f) => f.nombreTelefono + " ($" + dinero(Number(f.preciofinal) + Number(f.descuento)) + ")")
        .join(", ");
      document.getElementById("preciofinal").textContent = dinero(suma("preciofinal"));
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
