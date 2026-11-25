const soap = require("soap");

class SoapClient {
  constructor(wsdlUrl) {
    this.wsdlUrl = wsdlUrl;
  }

  createClient() {
    return new Promise((resolve, reject) => {
      soap.createClient(this.wsdlUrl, (err, client) => {
        if (err) {
          return reject(err);
        }
        resolve(client);
      });
    });
  }
}

module.exports = SoapClient;
