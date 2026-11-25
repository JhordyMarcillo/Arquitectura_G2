const compraService = require("./CompraService");
const telefonoService = require("./TelefonoService");
const loginService = require("./LoginService");

// Ejemplo de uso
(async () => {
  try {
    const resultadoCompraEfectivo = await compraService.comprarEfectivo(
      1,
      "1234567890"
    );
    console.log("Resultado Compra Efectivo:", resultadoCompraEfectivo);

    const resultadoCompraCredito = await compraService.comprarCredito(
      1,
      "1234567890",
      12
    );
    console.log("Resultado Compra Credito:", resultadoCompraCredito);

    const factura = await compraService.obtenerFactura("1234567890");
    console.log("Factura:", factura);

    const tablaAmortizacion = await compraService.consultarTablaAmortizacion(1);
    console.log("Tabla Amortizacion:", tablaAmortizacion);

    const telefono = await telefonoService.getTelefonoById(1);
    console.log("Telefono:", telefono);

    const allTelefonos = await telefonoService.getAllTelefonos();
    console.log("All Telefonos:", allTelefonos);

    const authResult = await loginService.auth("username", "password");
    console.log("Auth Result:", authResult);
  } catch (error) {
    console.error("Error:", error);
  }
})();
