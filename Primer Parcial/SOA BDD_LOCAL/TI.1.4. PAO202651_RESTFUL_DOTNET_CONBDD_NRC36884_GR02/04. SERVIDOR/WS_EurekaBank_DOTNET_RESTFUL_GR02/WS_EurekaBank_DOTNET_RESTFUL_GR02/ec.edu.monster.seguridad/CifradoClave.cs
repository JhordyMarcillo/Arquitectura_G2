using System.Security.Cryptography;
using System.Text;

namespace ec.edu.monster.seguridad
{
    /// <summary>Equivalente a SHA() de MySQL: SHA1 en hexadecimal en minusculas.</summary>
    public static class CifradoClave
    {
        public static string Sha1(string texto)
        {
            byte[] hash = SHA1.HashData(Encoding.UTF8.GetBytes(texto));
            return Convert.ToHexString(hash).ToLowerInvariant();
        }
    }
}
