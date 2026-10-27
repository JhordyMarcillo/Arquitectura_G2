namespace ec.edu.monster.Modelos;

public static class ConstantesRed
{
    public const string IP_WEB_SERVICES = "localhost";

    public const string UrlJavaSoap = $"http://{IP_WEB_SERVICES}:8080/WS_ConUni_Java_GR02/WSConversorUnidades";
    public const string UrlDotNetSoap = $"http://{IP_WEB_SERVICES}:50774/WSConversorUnidades.svc";
    public const string UrlDotNetRest = $"http://{IP_WEB_SERVICES}:5119/api";
    public const string UrlJavaRest = $"http://{IP_WEB_SERVICES}:8080/04.SERVIDOR/api";
}
