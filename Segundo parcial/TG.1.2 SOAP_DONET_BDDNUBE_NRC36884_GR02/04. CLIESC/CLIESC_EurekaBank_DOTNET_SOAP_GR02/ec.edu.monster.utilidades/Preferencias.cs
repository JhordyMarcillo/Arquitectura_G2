using System.IO;
using System.Text.Json;

namespace ec.edu.monster.utilidades
{
    /// <summary>Recuerda la URL del ultimo servidor usado (en %APPDATA%\EurekaBankGR02).</summary>
    public static class Preferencias
    {
        private static readonly string ARCHIVO = Path.Combine(
                Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), "EurekaBankGR02", "CLIESC_EurekaBank_DOTNET_SOAP_GR02.json");

        public static string LeerUrlServidor(string porDefecto)
        {
            try
            {
                if (File.Exists(ARCHIVO))
                {
                    Dictionary<string, string>? datos = JsonSerializer.Deserialize<Dictionary<string, string>>(File.ReadAllText(ARCHIVO));
                    if (datos is not null && datos.TryGetValue("urlServidor", out string? url) && !string.IsNullOrWhiteSpace(url))
                    {
                        return url;
                    }
                }
            }
            catch (Exception)
            {
                // Si el archivo esta danado se usa el valor por defecto
            }
            return porDefecto;
        }

        public static void GuardarUrlServidor(string url)
        {
            try
            {
                Directory.CreateDirectory(Path.GetDirectoryName(ARCHIVO)!);
                File.WriteAllText(ARCHIVO, JsonSerializer.Serialize(new Dictionary<string, string> { ["urlServidor"] = url }));
            }
            catch (Exception)
            {
                // No poder guardar la preferencia no impide usar la aplicacion
            }
        }
    }
}
