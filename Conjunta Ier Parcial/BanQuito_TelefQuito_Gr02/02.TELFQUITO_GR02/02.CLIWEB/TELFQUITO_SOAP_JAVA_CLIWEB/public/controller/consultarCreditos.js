import { showModal } from "./modal.js";

document.addEventListener("DOMContentLoaded", () => {
  document
    .getElementById("searchButton")
    .addEventListener("click", searchAmortizaciones);
});

async function searchAmortizaciones() {
  const cedula = document.getElementById("cedula").value;

  try {
    const response = await fetch("/consultarTablaAmortizacion", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ cedula: cedula }),
    });

    const result = await response.json();

    if (result.success) {
      populateAmortizaciones(result.result);
    } else {
      showModal("Error", "Fallo en recuperar las amortizaciones.");
    }
  } catch (error) {
    console.error("Search amortizaciones error:", error);
    showModal(
      "Error",
      "Un error ha ocurrido durante la búsqueda de las amortizaciones. Por favor, revise la conexión."
    );
  }
}

function populateAmortizaciones(amortizaciones) {
  const amortizacionesInfo = document.getElementById("movements-list");
  amortizacionesInfo.innerHTML = "";

  amortizaciones.forEach((amortizacion) => {
    const amortizacionItem = document.createElement("div");
    amortizacionItem.className = "movement-item";
    amortizacionItem.innerHTML = `
      <div class="row bg-radius2 bg-secondary-light py-3 mb-2">
        <div class="col-6">
         <div class="font-bold text-black-700">N# Cuota: ${amortizacion.cuota}</div>
          <div class="font-normal text-gray">Código de Credito: ${amortizacion.codCredito}</div>
         
          <div class="font-normal text-gray-700">Valor Cuota: ${amortizacion.valorCuota}</div>
        </div>
        <div class="col-6 d-flex flex-column align-items-end">
          <div class="font-normal text-gray-700">Interés Pagado: ${amortizacion.interesPagado}</div>
           <div class="font-normal text-gray-700">CapitalPagado: ${amortizacion.capitalPagado}</div>
          <div class="font-normal text-gray-700">Saldo: ${amortizacion.saldo}</div>
        </div>
      </div>
    `;
    amortizacionesInfo.appendChild(amortizacionItem);
  });
}
