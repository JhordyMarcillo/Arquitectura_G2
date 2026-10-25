using System;
using System.Configuration;
using System.ServiceModel;

namespace ec.edu.monster.servicios
{
    [ServiceContract(Name = "WSConversion", Namespace = "http://tempuri.org/")]
    public interface IWSConversion
    {
        [OperationContract(Name = "login")] bool Login(string usuario, string contrasena);

        [OperationContract(Name = "celsiusAFahrenheit")] double CelsiusAFahrenheit(double celsius);
        [OperationContract(Name = "fahrenheitACelsius")] double FahrenheitACelsius(double fahrenheit);
        [OperationContract(Name = "celsiusAKelvin")] double CelsiusAKelvin(double celsius);
        [OperationContract(Name = "kelvinACelsius")] double KelvinACelsius(double kelvin);
        [OperationContract(Name = "fahrenheitAKelvin")] double FahrenheitAKelvin(double fahrenheit);
        [OperationContract(Name = "kelvinAFahrenheit")] double KelvinAFahrenheit(double kelvin);

        [OperationContract(Name = "metrosAPies")] double MetrosAPies(double metros);
        [OperationContract(Name = "piesAMetros")] double PiesAMetros(double pies);
        [OperationContract(Name = "metrosAPulgadas")] double MetrosAPulgadas(double metros);
        [OperationContract(Name = "pulgadasAMetros")] double PulgadasAMetros(double pulgadas);
        [OperationContract(Name = "kilometrosAMillas")] double KilometrosAMillas(double kilometros);
        [OperationContract(Name = "millasAKilometros")] double MillasAKilometros(double millas);

        [OperationContract(Name = "kilogramosALibras")] double KilogramosALibras(double kilogramos);
        [OperationContract(Name = "librasAKilogramos")] double LibrasAKilogramos(double libras);
        [OperationContract(Name = "gramosAOnzas")] double GramosAOnzas(double gramos);
        [OperationContract(Name = "onzasAGramos")] double OnzasAGramos(double onzas);
    }

    /// <summary>Cliente SOAP: la URL se lee de App.config (clave UrlServicioSOAP).</summary>
    public class ClienteConversionSOAP : IDisposable
    {
        public string Url { get; }
        public IWSConversion Proxy { get; }
        private readonly ChannelFactory<IWSConversion> _factory;

        public ClienteConversionSOAP()
        {
            Url = ConfigurationManager.AppSettings["UrlServicioSOAP"];
            if (string.IsNullOrWhiteSpace(Url))
                throw new ConfigurationErrorsException("Falta la clave UrlServicioSOAP en App.config");

            var binding = new BasicHttpBinding
            {
                OpenTimeout = TimeSpan.FromSeconds(10),
                SendTimeout = TimeSpan.FromSeconds(30)
            };
            _factory = new ChannelFactory<IWSConversion>(binding, new EndpointAddress(Url));
            Proxy = _factory.CreateChannel();
        }

        public void Dispose()
        {
            try { _factory.Close(); } catch { _factory.Abort(); }
        }
    }
}
