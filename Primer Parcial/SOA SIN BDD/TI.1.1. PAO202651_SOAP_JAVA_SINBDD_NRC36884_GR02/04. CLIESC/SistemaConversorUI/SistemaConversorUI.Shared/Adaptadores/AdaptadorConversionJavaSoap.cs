using System.Globalization;
using System.Security;
using System.Text;
using System.Xml.Linq;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorConversionJavaSoap : IAdaptadorConversion
{
    public TipoBackend Tipo => TipoBackend.JavaSoap;
    private readonly HttpClient _httpClient;

    public AdaptadorConversionJavaSoap(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<double> ConvertirAsync(PeticionConversion peticion, string token)
    {
        // Determinamos qué método llamar en el Web Service
        string metodoWs = peticion.TipoConversion switch
        {
            "Masa" => "convertirMasa",
            "Temperatura" => "convertirTemperatura",
            _ => "convertirLongitud"
        };

        string valorFormateado = peticion.Valor.ToString(CultureInfo.InvariantCulture);

        // 1. Construir el sobre SOAP incluyendo el Token en el Body
        string soapXml = $@"<soapenv:Envelope xmlns:soapenv=""http://schemas.xmlsoap.org/soap/envelope/"" xmlns:ws=""http://ws.controladores.monster.edu.ec/"">
                               <soapenv:Header/>
                               <soapenv:Body>
                                  <ws:{metodoWs}>
                                     <token>{SecurityElement.Escape(token)}</token>
                                     <unidadOrigen>{SecurityElement.Escape(peticion.UnidadOrigen)}</unidadOrigen>
                                     <unidadDestino>{SecurityElement.Escape(peticion.UnidadDestino)}</unidadDestino>
                                     <valor>{valorFormateado}</valor>
                                  </ws:{metodoWs}>
                               </soapenv:Body>
                            </soapenv:Envelope>";

        var content = new StringContent(soapXml, Encoding.UTF8, "text/xml");

        // 2. Enviar la petición
        var response = await _httpClient.PostAsync(ConstantesRed.UrlJavaSoap, content);
        string responseXml = await response.Content.ReadAsStringAsync();

        // Si el token es inválido o hay un error, Java SOAP suele devolver HTTP 500 con un <soapenv:Fault>
        if (!response.IsSuccessStatusCode)
        {
            var docFault = XDocument.Parse(responseXml);
            var faultString = docFault.Descendants().FirstOrDefault(x => x.Name.LocalName == "faultstring")?.Value;
            throw new Exception($"Error del servidor SOAP: {faultString ?? response.StatusCode.ToString()}");
        }

        // 3. Leer la respuesta: el servidor informa los errores de negocio en <estado>/<mensaje>
        var doc = XDocument.Parse(responseXml);
        string? Campo(string nombre) => doc.Descendants().FirstOrDefault(x => x.Name.LocalName == nombre)?.Value;

        if (Campo("estado") != "OK")
            throw new Exception(Campo("mensaje") ?? "El servidor no pudo realizar la conversión.");

        if (double.TryParse(Campo("resultado"), NumberStyles.Any, CultureInfo.InvariantCulture, out double resultado))
        {
            return resultado;
        }

        throw new Exception("No se pudo parsear el resultado matemático del XML.");
    }
}