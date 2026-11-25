using System.Net;
using ApiPaisesMVC_SQLServer.Models;
using Newtonsoft.Json.Linq;

namespace ApiPaisesMVC_SQLServer.Services
{
    /// <summary>Error al consumir la API (sin key, key invalida, limite, red...).</summary>
    public class ApiException : Exception
    {
        public ApiException(string mensaje, Exception inner = null) : base(mensaje, inner) { }
    }

    /// <summary>
    /// Consume REST Countries v5. Requiere API key en el header Authorization: Bearer.
    /// Respuesta: { "data": { "objects": [...], "meta": { "total": N } } }
    /// </summary>
    public class PaisApiService
    {
        // Solo pedimos los campos que necesita el taller
        private const string Campos =
            "names.common,capitals,region,subregion,population,area,currencies";
        public const int Limite = 100; // maximo por pagina en el plan gratuito

        private readonly HttpClient _http;
        private readonly string _apiKey;

        public PaisApiService(HttpClient http, IConfiguration config)
        {
            _http = http;
            _http.BaseAddress = new Uri(
                config["RestCountries:BaseUrl"] ?? "https://api.restcountries.com/countries/v5");
            _http.Timeout = TimeSpan.FromSeconds(30);
            _apiKey = config["RestCountries:ApiKey"] ?? "";
        }

        /// <summary>Descarga TODOS los paises, pagina por pagina.</summary>
        public async Task<List<Pais>> ObtenerPaises()
        {
            var lista = new List<Pais>();
            int offset = 0, total = int.MaxValue;
            while (offset < total)
            {
                JObject resp = await Llamar($"?limit={Limite}&offset={offset}&response_fields={Campos}");
                total = Total(resp);
                var pagina = Parsear(resp);
                if (pagina.Count == 0) break;
                lista.AddRange(pagina);
                offset += Limite;
            }
            return lista;
        }

        /// <summary>Actividad 1: consulta paises por nombre en la API (sin guardar).</summary>
        public async Task<List<Pais>> BuscarPorNombre(string nombre)
        {
            if (string.IsNullOrWhiteSpace(nombre))
                throw new ApiException("Escribe el nombre del país a buscar.");
            JObject resp = await Llamar(
                $"?q={Uri.EscapeDataString(nombre.Trim())}&limit={Limite}&response_fields={Campos}");
            return Parsear(resp);
        }

        private async Task<JObject> Llamar(string consulta)
        {
            if (string.IsNullOrEmpty(_apiKey))
                throw new ApiException(
                    "Falta la API key de REST Countries (https://restcountries.com/sign-up). " +
                    "Configúrala con: dotnet user-secrets set \"RestCountries:ApiKey\" \"<tu key>\"");

            var req = new HttpRequestMessage(HttpMethod.Get, consulta);
            req.Headers.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", _apiKey);
            try
            {
                using var resp = await _http.SendAsync(req);
                string cuerpo = await resp.Content.ReadAsStringAsync();
                if (resp.StatusCode == HttpStatusCode.Unauthorized || resp.StatusCode == HttpStatusCode.Forbidden)
                    throw new ApiException($"La API rechazó la key (HTTP {(int)resp.StatusCode}). Revisa que sea válida.");
                if (resp.StatusCode == (HttpStatusCode)429)
                    throw new ApiException("Se superó el límite de solicitudes (HTTP 429). Espera un momento.");
                if (!resp.IsSuccessStatusCode)
                    throw new ApiException($"La API respondió con error HTTP {(int)resp.StatusCode}: {cuerpo}");
                return JObject.Parse(cuerpo);
            }
            catch (HttpRequestException ex)
            {
                throw new ApiException("No se pudo conectar con la API: " + ex.Message, ex);
            }
            catch (TaskCanceledException ex)
            {
                throw new ApiException("La API tardó demasiado en responder (timeout).", ex);
            }
        }

        public static int Total(JObject resp) =>
            (int?)resp["data"]?["meta"]?["total"] ?? (int?)resp["meta"]?["total"] ?? 0;

        public static List<Pais> Parsear(JObject resp)
        {
            var lista = new List<Pais>();
            if (resp["data"]?["objects"] is JArray objetos)
                foreach (var item in objetos)
                    lista.Add(AUnPais(item));
            return lista;
        }

        /// <summary>Un objeto pais de la API v5 -> Pais. Tolera campos ausentes.</summary>
        public static Pais AUnPais(JToken item)
        {
            // capital: la marcada como primary; si no hay, la primera
            string capital = null;
            if (item["capitals"] is JArray caps)
            {
                foreach (var c in caps)
                {
                    string n = (string)c["name"];
                    bool primary = (bool?)c["attributes"]?["primary"] ?? false;
                    if (capital == null || primary) capital = n;
                    if (primary) break;
                }
            }

            // moneda: "Canadian dollar (CAD)"; varias separadas por coma
            var monedas = new List<string>();
            if (item["currencies"] is JArray mons)
                foreach (var m in mons)
                {
                    string nombre = (string)m["name"] ?? "";
                    string codigo = (string)m["code"] ?? "";
                    monedas.Add(codigo == "" ? nombre : $"{nombre} ({codigo})");
                }

            return new Pais
            {
                Nombre = (string)item["names"]?["common"] ?? "",
                Capital = capital ?? "Sin capital",
                Region = (string)item["region"] ?? "",
                SubRegion = (string)item["subregion"] ?? "",
                Poblacion = (long?)item["population"] ?? 0L,
                Area = (double?)item["area"]?["kilometers"] ?? 0d,
                Moneda = monedas.Count > 0 ? string.Join(", ", monedas) : "N/D"
            };
        }
    }
}
