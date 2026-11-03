const SoapClient = require("./soapclient");

class CompraService {
  constructor() {
    this.wsdlUrl = "http://localhost:8080/TELFQUITO_SOAP_JAVA/WSCompra?wsdl";
    this.namespace = "http://ws.monster.edu.ec/";
  }

  async comprarEfectivo(codTelefono, codCedula) {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        const args = {
          codTelefono: codTelefono,
          codcCedula: codCedula,
        };

        client.comprarEfectivo(args, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          resolve(result.return);
        });
      });
    } catch (error) {
      console.error("Compra Efectivo Error:", error);
      throw error;
    }
  }

  async comprarCredito(codTelefono, cedula, plazoMeses) {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        const args = {
          codTelefono: codTelefono,
          cedula: cedula,
          plazoMeses: plazoMeses,
        };

        client.comprarCredito(args, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          resolve(result.return);
        });
      });
    } catch (error) {
      console.error("Compra Credito Error:", error);
      throw error;
    }
  }

  async obtenerFactura(cedula) {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        const args = {
          cedula: cedula,
        };

        client.obtenerFactura(args, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          resolve(result.return);
        });
      });
    } catch (error) {
      console.error("Obtener Factura Error:", error);
      throw error;
    }
  }

  async consultarTablaAmortizacion(cedula) {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        const args = {
          cedula: cedula,
        };

        client.consultarTablaAmortizacion(args, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          if (result && result.return) {
            resolve(result.return);
          } else {
            console.error("SOAP Response Error: No return value");
            return reject(new Error("No return value from SOAP response"));
          }
        });
      });
    } catch (error) {
      console.error("Error in consultarTablaAmortizacion:", error);
      throw error;
    }
  }
}

module.exports = new CompraService();
