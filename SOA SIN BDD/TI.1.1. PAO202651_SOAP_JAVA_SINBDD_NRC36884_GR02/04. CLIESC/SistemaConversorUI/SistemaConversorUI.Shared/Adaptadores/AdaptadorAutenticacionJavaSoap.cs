using System.Security;
using System.Text;
using System.Xml.Linq;
using ec.edu.monster.Interfaces;
using ec.edu.monster.Modelos;

namespace ec.edu.monster.Adaptadores;

public class AdaptadorAutenticacionJavaSoap : IAdaptadorAutenticacion
{
    public TipoBackend Tipo => TipoBackend.JavaSoap;
    private readonly HttpClient _httpClient;

    public AdaptadorAutenticacionJavaSoap(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<string> IniciarSesionAsync(string usuario, string contrasenia)
    {
        string soapXml = $@"<soapenv:Envelope xmlns:soapenv=""http://schemas.xmlsoap.org/soap/envelope/"" xmlns:ws=""http://ws.controladores.monster.edu.ec/"">
                               <soapenv:Header/>
                               <soapenv:Body>
                                  <ws:autenticar>
                                     <usuario>{SecurityElement.Escape(usuario)}</usuario>
                                     <contrasenia>{SecurityElement.Escape(contrasenia)}</contrasenia>
                                  </ws:autenticar>
                               </soapenv:Body>
                            </soapenv:Envelope>";

        var content = new StringContent(soapXml, Encoding.UTF8, "text/xml");
        var response = await _httpClient.PostAsync(ConstantesRed.UrlJavaSoap, content);
        string responseXml = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
            throw new Exception($"Error del servidor SOAP: {ObtenerFault(responseXml) ?? response.StatusCode.ToString()}");

        var doc = XDocument.Parse(responseXml);
        string? Campo(string nombre) => doc.Descendants().FirstOrDefault(x => x.Name.LocalName == nombre)?.Value;

        if (Campo("estado") != "OK")
            throw new Exception(Campo("mensaje") ?? "No se pudo autenticar.");

        var token = Campo("token");
        if (string.IsNullOrEmpty(token))
            throw new Exception("No se encontró el token en la respuesta SOAP.");

        return token;
    }

    public Task<string> CambiarContraseniaAsync(string contraseniaActual, string contraseniaNueva, string token) =>
        throw new NotSupportedException("El servidor Java SOAP no ofrece cambio de contraseña.");

    private static string? ObtenerFault(string xml)
    {
        try
        {
            return XDocument.Parse(xml).Descendants().FirstOrDefault(x => x.Name.LocalName == "faultstring")?.Value;
        }
        catch (System.Xml.XmlException)
        {
            return null;
        }
    }
}
