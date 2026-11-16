import { showModal } from "./modal.js";

document.addEventListener("DOMContentLoaded", () => {
  document
    .getElementById("crudTelefonosButton")
    .addEventListener("click", () => {
      window.location.href = "/crudTelefonos";
    });

  document
    .getElementById("consultarCreditosButton")
    .addEventListener("click", () => {
      window.location.href = "/consultarCreditos";
    });

  fetchAllTelefonos(); // Fetch all teléfonos when the page loads
});

async function fetchAllTelefonos() {
  try {
    const response = await fetch("/getAllTelefonos");
    const result = await response.json();

    if (result.success) {
      populateTelefonos(result.result);
    } else {
      showModal("Error", "Fallo en recuperar los teléfonos.");
    }
  } catch (error) {
    console.error("Fetch all telefonos error:", error);
    showModal(
      "Error",
      "Un error ha ocurrido durante la recuperación de los teléfonos. Por favor, revise la conexión."
    );
  }
}
function editTelefono(id) {
  window.location.href = `/editarTelefonos?id=${id}`;
}
function buyTelefono(id) {
  window.location.href = `/comprarTelefono?id=${id}`;
}
function populateTelefonos(telefonos) {
  const telefonoInfo = document.getElementById("movements-list");
  telefonoInfo.innerHTML = "";

  telefonos.forEach((telefono) => {
    const telefonoItem = document.createElement("div");
    telefonoItem.className = 'movement-item';
    telefonoItem.innerHTML = `
      <div class="col-6">
        <div class="font-bold text-black">ID: ${telefono.codTelefono}</div>
        <div class="font-normal text-gray-700">Nombre: ${telefono.nombre}</div>
        <div class="font-normal text-gray-700">Precio: $${telefono.precio}</div>
        <div class="font-normal text-gray-700">Marca: ${telefono.marca}</div>
        <div class="font-normal text-gray-700">Disponible: ${telefono.disponible}</div>
      </div>
      <div>
        <div class="d-flex justify-content-end mt-2">
          <button class="btn btn-primary mr-2 btn-edit">Editar</button>
          <br/><br/>
          <button class="btn btn-primary mr-2 btn-buy">Comprar</button>
        </div>
      </div>
    `;

    // Asignar eventos a los botones
    const editButton = telefonoItem.querySelector(".btn-edit");
    const buyButton = telefonoItem.querySelector(".btn-buy");

    editButton.addEventListener("click", () =>
      editTelefono(telefono.codTelefono)
    );
    buyButton.addEventListener("click", () =>
      buyTelefono(telefono.codTelefono)
    );

    telefonoInfo.appendChild(telefonoItem);
  });
}
