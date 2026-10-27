namespace ec.edu.monster.modelos.utilidades
{
    public static class CodigosRespuesta
    {
        public const string ESTADO_OK = "OK";
        public const string ESTADO_ERROR = "ERROR";

        public const string EXITO = "200";
        public const string TOKEN_INVALIDO = "AUTH-001";
        public const string SESION_EXPIRADA = "AUTH-002";
        public const string CREDENCIALES_INCORRECTAS = "AUTH-003";
        public const string UNIDAD_NO_SOPORTADA = "UNIT-001";
        public const string DATOS_INCONSISTENTES = "DATA-001";
        public const string ERROR_SERVIDOR = "SRV-001";
    }
}
