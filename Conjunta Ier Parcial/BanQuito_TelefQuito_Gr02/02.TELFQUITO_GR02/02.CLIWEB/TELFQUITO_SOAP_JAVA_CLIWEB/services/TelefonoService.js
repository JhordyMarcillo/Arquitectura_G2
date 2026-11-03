const SoapClient = require("./soapclient");

class TelefonoService {
  constructor() {
    this.wsdlUrl = "http://localhost:8080/TELFQUITO_SOAP_JAVA/WSTelefonos?wsdl";
    this.namespace = "http://ws.monster.edu.ec/";
  }

  async getTelefonoById(id) {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        const args = {
          id: id,
        };

        client.getTelefonoById(args, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          resolve(result.return);
        });
      });
    } catch (error) {
      console.error("Get Telefono By Id Error:", error);
      throw error;
    }
  }

  async insertTelefono(telefono) {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        const args = {
          telefono: telefono,
        };

        client.insertTelefono(args, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          resolve(result.return);
        });
      });
    } catch (error) {
      console.error("Insert Telefono Error:", error);
      throw error;
    }
  }

  async updateTelefono(telefono) {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        const args = {
          telefono: telefono,
        };

        client.updateTelefono(args, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          resolve(result.return);
        });
      });
    } catch (error) {
      console.error("Update Telefono Error:", error);
      throw error;
    }
  }

  async getAllTelefonos() {
    try {
      const soapClient = new SoapClient(this.wsdlUrl);
      const client = await soapClient.createClient();

      return new Promise((resolve, reject) => {
        client.getAllTelefonos({}, (err, result) => {
          if (err) {
            console.error("SOAP Request Error:", err);
            return reject(err);
          }
          resolve(result.return);
        });
      });
    } catch (error) {
      console.error("Get All Telefonos Error:", error);
      throw error;
    }
  }
}

module.exports = new TelefonoService();
