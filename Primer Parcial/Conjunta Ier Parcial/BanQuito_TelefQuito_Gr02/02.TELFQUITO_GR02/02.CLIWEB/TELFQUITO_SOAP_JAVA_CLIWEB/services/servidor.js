// Direccion del servidor TelefQuito: variable de entorno SERVIDOR_IP ("ip" o "ip:puerto").
// Sin la variable se usa localhost:8080.
const ip = (process.env.SERVIDOR_IP || "localhost").trim();
const host = ip.includes(":") ? ip : `${ip}:8080`;

module.exports = {
  host,
  wsdl: (servicio) => `http://${host}/TELFQUITO_SOAP_JAVA/${servicio}?wsdl`,
};
