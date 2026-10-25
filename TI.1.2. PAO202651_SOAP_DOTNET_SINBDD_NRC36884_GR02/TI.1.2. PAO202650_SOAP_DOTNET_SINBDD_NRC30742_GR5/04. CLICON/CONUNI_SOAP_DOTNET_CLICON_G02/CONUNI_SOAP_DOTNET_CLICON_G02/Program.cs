using System;
using System.Collections.Generic;
using System.Globalization;
using ec.edu.monster.servicios;

namespace CONUNI_SOAP_DOTNET_CLICON_G09
{
    class Program
    {
        class Opcion
        {
            public string Texto;
            public Func<IWSConversion, double, double> Operacion;
            public Opcion(string texto, Func<IWSConversion, double, double> op) { Texto = texto; Operacion = op; }
        }

        static readonly Dictionary<string, List<Opcion>> Menu = new Dictionary<string, List<Opcion>>
        {
            { "Temperatura", new List<Opcion> {
                new Opcion("Celsius -> Fahrenheit", (s, v) => s.CelsiusAFahrenheit(v)),
                new Opcion("Fahrenheit -> Celsius", (s, v) => s.FahrenheitACelsius(v)),
                new Opcion("Celsius -> Kelvin", (s, v) => s.CelsiusAKelvin(v)),
                new Opcion("Kelvin -> Celsius", (s, v) => s.KelvinACelsius(v)),
                new Opcion("Fahrenheit -> Kelvin", (s, v) => s.FahrenheitAKelvin(v)),
                new Opcion("Kelvin -> Fahrenheit", (s, v) => s.KelvinAFahrenheit(v)) } },
            { "Longitud", new List<Opcion> {
                new Opcion("Metros -> Pies", (s, v) => s.MetrosAPies(v)),
                new Opcion("Pies -> Metros", (s, v) => s.PiesAMetros(v)),
                new Opcion("Metros -> Pulgadas", (s, v) => s.MetrosAPulgadas(v)),
                new Opcion("Pulgadas -> Metros", (s, v) => s.PulgadasAMetros(v)),
                new Opcion("Kilometros -> Millas", (s, v) => s.KilometrosAMillas(v)),
                new Opcion("Millas -> Kilometros", (s, v) => s.MillasAKilometros(v)) } },
            { "Peso", new List<Opcion> {
                new Opcion("Kilogramos -> Libras", (s, v) => s.KilogramosALibras(v)),
                new Opcion("Libras -> Kilogramos", (s, v) => s.LibrasAKilogramos(v)),
                new Opcion("Gramos -> Onzas", (s, v) => s.GramosAOnzas(v)),
                new Opcion("Onzas -> Gramos", (s, v) => s.OnzasAGramos(v)) } }
        };

        static void Main(string[] args)
        {
            Console.WriteLine("=== CLIENTE SOAP MONSTERS INC. CONVERTER ===");
            try
            {
                using (var cliente = new ClienteConversionSOAP())
                {
                    Console.WriteLine("Servidor: " + cliente.Url);
                    if (Autenticar(cliente.Proxy)) Ejecutar(cliente.Proxy);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("Error: " + ex.Message);
            }
            Console.WriteLine("Hasta luego.");
        }

        static bool Autenticar(IWSConversion servicio)
        {
            for (int intento = 1; intento <= 3; intento++)
            {
                Console.Write("Usuario: ");
                string usuario = Console.ReadLine();
                Console.Write("Contrasena: ");
                string clave = Console.ReadLine();
                try
                {
                    if (servicio.Login(usuario, clave)) { Console.WriteLine("Acceso concedido.\n"); return true; }
                    Console.WriteLine("Credenciales incorrectas.");
                }
                catch (Exception ex)
                {
                    Console.WriteLine("No se pudo contactar al servidor: " + ex.Message);
                    return false;
                }
            }
            return false;
        }

        static void Ejecutar(IWSConversion servicio)
        {
            var categorias = new List<string>(Menu.Keys);
            while (true)
            {
                Console.WriteLine("--- MENU ---");
                for (int i = 0; i < categorias.Count; i++) Console.WriteLine((i + 1) + ". " + categorias[i]);
                Console.WriteLine("0. Salir");
                Console.Write("Opcion: ");
                int c;
                if (!int.TryParse(Console.ReadLine(), out c) || c < 0 || c > categorias.Count) { Console.WriteLine("Opcion invalida."); continue; }
                if (c == 0) return;

                var ops = Menu[categorias[c - 1]];
                for (int i = 0; i < ops.Count; i++) Console.WriteLine("  " + (i + 1) + ". " + ops[i].Texto);
                Console.Write("Conversion: ");
                int o;
                if (!int.TryParse(Console.ReadLine(), out o) || o < 1 || o > ops.Count) { Console.WriteLine("Opcion invalida."); continue; }

                Console.Write("Valor: ");
                string txt = (Console.ReadLine() ?? "").Replace(',', '.');
                double valor;
                if (!double.TryParse(txt, NumberStyles.Float, CultureInfo.InvariantCulture, out valor)) { Console.WriteLine("Valor invalido."); continue; }
                try
                {
                    double r = ops[o - 1].Operacion(servicio, valor);
                    Console.WriteLine("Resultado: " + valor.ToString(CultureInfo.InvariantCulture) + " -> " +
                        r.ToString(CultureInfo.InvariantCulture) + "  (" + ops[o - 1].Texto + ")\n");
                }
                catch (Exception ex) { Console.WriteLine("Error al convertir: " + ex.Message); }
            }
        }
    }
}
