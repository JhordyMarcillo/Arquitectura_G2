import { showModal } from "./modal.js";

document.addEventListener("DOMContentLoaded", () => {
  const urlParams = new URLSearchParams(window.location.search);
  const telefonoId = urlParams.get("id");

  if (telefonoId) {
    loadTelefonoData(telefonoId);
  }

  document.getElementById("tipoCompra").addEventListener("change", (e) => {
    const tipoCompra = e.target.value;
    document.querySelectorAll(".compra-opcion").forEach((el) => {
      el.classList.add("hidden");
      el.querySelectorAll("input").forEach((input) => {
        input.removeAttribute("required");
      });
    });
    if (tipoCompra === "efectivo") {
      const compraEfectivo = document.getElementById("compraEfectivo");
      compraEfectivo.classList.remove("hidden");
      // compraEfectivo
      //   .querySelector("input")
      //   .setAttribute("required", "required");

      const precioText = document.getElementById("precio").textContent;
      const precio = parseFloat(precioText.replace(/[^0-9.-]+/g, ""));
      const descuento = precio * 0.42;
      const precioFinal = precio - descuento;
      document.getElementById("precioOriginal").textContent =
        "$" + precio.toFixed(2);
      document.getElementById("descuento").textContent =
        "$" + descuento.toFixed(2);
      document.getElementById("precioFinal").textContent =
        "$" + precioFinal.toFixed(2);
    } else if (tipoCompra === "credito") {
      const compraCredito = document.getElementById("compraCredito");
      compraCredito.classList.remove("hidden");
      compraCredito.querySelectorAll("input").forEach((input) => {
        input.setAttribute("required", "required");
      });
    }
  });

  document
    .getElementById("compraForm")
    .addEventListener("submit", async (e) => {
      e.preventDefault();

      const tipoCompra = document.getElementById("tipoCompra").value;
      const codTelefono = parseInt(
        document.getElementById("telefonoId").value,
        10
      );
      const cedula =
        tipoCompra === "efectivo"
          ? document.getElementById("cedula").value
          : document.getElementById("cedulaCredito").value;
      const plazoMeses =
        tipoCompra === "credito"
          ? parseInt(document.getElementById("plazoMeses").value, 10)
          : null;

      if (tipoCompra === "credito" && (plazoMeses < 3 || plazoMeses > 18)) {
        showModal(
          "Advertencia",
          "El plazo debe estar entre 3 y 18 meses.",
          true,
          null
        );
        return;
      }

      try {
        const response = await fetch(
          tipoCompra === "efectivo" ? "/comprarEfectivo" : "/comprarCredito",
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              codTelefono,
              cedula,
              ...(tipoCompra === "credito" && { plazoMeses }),
            }),
          }
        );

        const result = await response.json();
        console.log(result);
        if (result.success) {
          if (
            tipoCompra === "credito" &&
            result.result === "Compra exitosa a credito!!"
          ) {
            showModal("Éxito", "Compra realizada exitosamente.", false, () => {
              window.location.href = `/factura?cedula=${cedula}`;
            });
          } else if (result.result === "Compra en efectivo exitosa!!") {
            showModal("Resultado", result.result, false, () => {
              window.location.href = `/factura?cedula=${cedula}`;
            });
          } else {
            showModal("Resultado", result.result, false, null);
          }
        } else {
          showModal("Error", "Fallo al realizar la compra.", true, null);
        }
      } catch (error) {
        console.error("Compra error:", error);
        showModal("Error", error.message, true, null);
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
      document.getElementById("nombre").textContent = telefono.nombre;
      document.getElementById("precio").textContent = "$" + telefono.precio;
      document.getElementById("marca").textContent = telefono.marca;
    } else {
      showModal(
        "Error",
        "Fallo en recuperar los datos del teléfono.",
        true,
        null
      );
    }
  } catch (error) {
    console.error("Load telefono data error:", error);
    showModal("Error", error.message, true, null);
  }
}
