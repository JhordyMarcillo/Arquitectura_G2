using ApiPaisesMVC_SQLServer.Models;
using Newtonsoft.Json.Linq;

namespace ApiPaisesMVC_SQLServer.Services
{
    /// <summary>Actividad avanzada: API publica de universidades (no requiere key).</summary>
    public class UniversidadApiService
    {
        private readonly HttpClient _http;

        public UniversidadApiService(HttpClient http)
        {
            _http = http;
            _http.Timeout = TimeSpan.FromSeconds(30);
        }

        public async Task<List<Universidad>> ObtenerPorPais(string pais)
        {
            if (string.IsNullOrWhiteSpace(pais))
                throw new ApiException("Escribe el nombre del país (en inglés, ej. Ecuador).");
            string url = "http://universities.hipolabs.com/search?country=" + Uri.EscapeDataString(pais.Trim());
            try
            {
                string json = await _http.GetStringAsync(url);
                return Parsear(JArray.Parse(json));
            }
            catch (HttpRequestException ex)
            {
                throw new ApiException("No se pudo consultar la API de universidades: " + ex.Message, ex);
            }
            catch (TaskCanceledException ex)
            {
                throw new ApiException("La API de universidades tardó demasiado (timeout).", ex);
            }
        }

        public static List<Universidad> Parsear(JArray arreglo)
        {
            var lista = new List<Universidad>();
            foreach (var u in arreglo)
            {
                lista.Add(new Universidad
                {
                    Nombre = (string)u["name"] ?? "",
                    Dominio = (u["domains"] as JArray)?.FirstOrDefault()?.ToString() ?? "",
                    SitioWeb = (u["web_pages"] as JArray)?.FirstOrDefault()?.ToString() ?? "",
                    Pais = (string)u["country"] ?? ""
                });
            }
            return lista;
        }
    }
}
