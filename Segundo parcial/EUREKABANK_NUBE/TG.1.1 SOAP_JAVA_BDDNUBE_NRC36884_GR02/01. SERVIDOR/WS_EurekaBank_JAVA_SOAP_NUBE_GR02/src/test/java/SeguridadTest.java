import static org.junit.jupiter.api.Assertions.assertEquals;

import ec.edu.monster.modelos.utilidades.enums.EstadoToken;
import ec.edu.monster.seguridad.AdministradorTokens;
import ec.edu.monster.seguridad.CifradoClave;
import org.junit.jupiter.api.Test;

class SeguridadTest {

    @Test
    void sha1CoincideConElShaDeMySQL() {
        assertEquals("1c0108a5c6cda33ab8dca984c29c7fe86b5ccbf9", CifradoClave.sha1("chicho"));
    }

    @Test
    void tokenGeneradoEsValido() {
        String token = AdministradorTokens.generarToken("0001", "Carlos Romero");
        assertEquals(EstadoToken.VALIDO, AdministradorTokens.validar(token));
        assertEquals("0001", AdministradorTokens.obtenerSesion(token).codigoEmpleado());
    }

    @Test
    void tokenDesconocidoEsInvalido() {
        assertEquals(EstadoToken.INVALIDO, AdministradorTokens.validar("no-existe"));
        assertEquals(EstadoToken.INVALIDO, AdministradorTokens.validar(null));
    }
}
