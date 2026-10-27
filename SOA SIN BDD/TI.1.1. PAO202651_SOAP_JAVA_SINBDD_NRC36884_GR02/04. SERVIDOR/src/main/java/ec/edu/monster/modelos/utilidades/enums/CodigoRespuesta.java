package ec.edu.monster.modelos.utilidades.enums;

public enum CodigoRespuesta {
    OK("200"),
    TOKEN_INVALIDO("AUTH-001"),
    CREDENCIALES_INCORRECTAS("AUTH-002"),
    UNIDAD_NO_SOPORTADA("UNIT-001"),
    DATOS_INCONSISTENTES("DATA-001"),
    ERROR_INTERNO("SYS-001");

    private final String codigo;

    CodigoRespuesta(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
