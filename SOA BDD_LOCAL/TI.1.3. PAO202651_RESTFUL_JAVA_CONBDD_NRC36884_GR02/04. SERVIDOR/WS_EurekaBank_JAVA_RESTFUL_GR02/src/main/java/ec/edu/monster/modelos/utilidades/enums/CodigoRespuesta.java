package ec.edu.monster.modelos.utilidades.enums;

/** Codigos del contrato con su estado HTTP. */
public enum CodigoRespuesta {
    EXITO("200", 200),
    TOKEN_INVALIDO("AUTH-001", 401),
    SESION_EXPIRADA("AUTH-002", 401),
    CREDENCIALES_INCORRECTAS("AUTH-003", 401),
    CUENTA_NO_EXISTE("CTA-001", 404),
    CUENTA_NO_ACTIVA("CTA-002", 409),
    CLAVE_CUENTA_INCORRECTA("CTA-003", 401),
    SALDO_INSUFICIENTE("CTA-004", 409),
    MONEDAS_DISTINTAS("CTA-005", 409),
    MISMA_CUENTA("CTA-006", 400),
    DATOS_INCONSISTENTES("DATA-001", 400),
    ERROR_SERVIDOR("SRV-001", 500);

    private final String codigo;
    private final int estadoHttp;

    CodigoRespuesta(String codigo, int estadoHttp) {
        this.codigo = codigo;
        this.estadoHttp = estadoHttp;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getEstadoHttp() {
        return estadoHttp;
    }
}
