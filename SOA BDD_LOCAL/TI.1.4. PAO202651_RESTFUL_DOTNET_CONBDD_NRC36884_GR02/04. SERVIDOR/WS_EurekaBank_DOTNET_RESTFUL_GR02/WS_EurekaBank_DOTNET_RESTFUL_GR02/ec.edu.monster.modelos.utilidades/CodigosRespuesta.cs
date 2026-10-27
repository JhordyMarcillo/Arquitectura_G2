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
        public const string CUENTA_NO_EXISTE = "CTA-001";
        public const string CUENTA_NO_ACTIVA = "CTA-002";
        public const string CLAVE_CUENTA_INCORRECTA = "CTA-003";
        public const string SALDO_INSUFICIENTE = "CTA-004";
        public const string MONEDAS_DISTINTAS = "CTA-005";
        public const string MISMA_CUENTA = "CTA-006";
        public const string DATOS_INCONSISTENTES = "DATA-001";
        public const string ERROR_SERVIDOR = "SRV-001";
    }
}
