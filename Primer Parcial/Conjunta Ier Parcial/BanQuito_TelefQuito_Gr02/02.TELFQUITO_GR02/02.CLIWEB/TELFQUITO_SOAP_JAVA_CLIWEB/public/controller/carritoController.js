import { showModal } from "./modal.js";

const MAX = 3;

document.addEventListener("DOMContentLoaded", async () => {
  const lista = document.getElementById("listaTelefonos");
  const resumen = document.getElementById("resumen");
  const tipo = document.getElementById("tipoCompra");
  const plazo = document.getElementById("plazoMeses");

  try {
    const r = await (await fetch("/getAllTelefonos")).json();
    const telefonos = (r.result || []).filter((t) => t.disponible === 1);
    lista.innerHTML = telefonos
      .map(
        (t) =>
          `<label style="display:block;margin:4px 0"><input type="checkbox" value="${t.codTelefono}"> ` +
          `${t.nombre} (${t.marca}) - $${t.precio}</label>`
      )
      .join("");
  } catch (e) {
    showModal("Error", "No se pudo cargar el listado de teléfonos.", true, null);
  }

  const marcados = () => [...lista.querySelectorAll("input:checked")];

  lista.addEventListener("change", (e) => {
    if (marcados().length > MAX) {
      e.target.checked = false;
      showModal("Advertencia", `Solo se pueden comprar hasta ${MAX} productos.`, true, null);
    }
    resumen.innerHTML = `<strong>Seleccionados: ${marcados().length} / ${MAX}</strong>`;
  });

  tipo.addEventListener("change", () => {
    plazo.classList.toggle("hidden", tipo.value !== "credito");
  });

  document.getElementById("carritoForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const codigos = marcados().map((c) => c.value);
    if (codigos.length < 1) {
      showModal("Advertencia", "Seleccione al menos un teléfono.", true, null);
      return;
    }
    const plazoMeses = parseInt(plazo.value, 10);
    if (tipo.value === "credito" && !(plazoMeses >= 3 && plazoMeses <= 18)) {
      showModal("Advertencia", "El plazo debe estar entre 3 y 18 meses.", true, null);
      return;
    }
    try {
      const res = await fetch("/comprarVarios", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          tipo: tipo.value,
          codTelefonos: codigos.join(","),
          cedula: document.getElementById("cedula").value,
          plazoMeses,
        }),
      });
      const data = await res.json();
      if (data.success && String(data.result).startsWith("FACTURA")) {
        const f = document.getElementById("factura");
        f.textContent = data.result;
        f.style.display = "block";
        document.getElementById("imprimir").style.display = "block";
        document.getElementById("carritoForm").style.display = "none";
      } else {
        showModal("Resultado", data.result || "Fallo al realizar la compra.", true, null);
      }
    } catch (err) {
      showModal("Error", err.message, true, null);
    }
  });
});
