import ec.edu.monster.modelos.utilidades.enums.CodigoRespuesta;
import ec.edu.monster.modelos.utilidades.enums.CategoriaUnidad;
import ec.edu.monster.modelos.utilidades.ExcepcionValidacion;
import ec.edu.monster.modelos.utilidades.ValidadorConversion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidadorConversionTest {

    @Test
    void deberiaRechazarUnidadNoSoportada() {
        ExcepcionValidacion e = assertThrows(ExcepcionValidacion.class,
                () -> ValidadorConversion.validarUnidad("XYZ", "origen", CategoriaUnidad.LONGITUD));

        assertEquals(CodigoRespuesta.UNIDAD_NO_SOPORTADA, e.getCodigo());
        assertEquals("Unidad de origen no soportada: XYZ", e.getMessage());
    }

    @Test
    void deberiaRechazarCategoriasIncompatibles() {
        ExcepcionValidacion e = assertThrows(ExcepcionValidacion.class,
                () -> ValidadorConversion.validarUnidad("CELSIUS", "destino", CategoriaUnidad.LONGITUD));

        assertEquals(CodigoRespuesta.DATOS_INCONSISTENTES, e.getCodigo());
    }

    @Test
    void deberiaRechazarDatosIncompletos() {
        assertThrows(ExcepcionValidacion.class, () -> ValidadorConversion.validarValor(null));
        assertThrows(ExcepcionValidacion.class, () -> ValidadorConversion.validarValor(Double.NaN));
        assertThrows(ExcepcionValidacion.class,
                () -> ValidadorConversion.validarUnidad(null, "origen", CategoriaUnidad.MASA));
    }

    @Test
    void deberiaRechazarValoresFueraDeRango() {
        assertThrows(ExcepcionValidacion.class,
                () -> ValidadorConversion.validarRango(-1, CategoriaUnidad.MASA, "KILOGRAMO"));
        assertThrows(ExcepcionValidacion.class,
                () -> ValidadorConversion.validarRango(-300, CategoriaUnidad.TEMPERATURA, "CELSIUS"));
        assertDoesNotThrow(
                () -> ValidadorConversion.validarRango(-459.67, CategoriaUnidad.TEMPERATURA, "FAHRENHEIT"));
    }

    @Test
    void deberiaAceptarUnidadesValidas() {
        assertDoesNotThrow(
                () -> ValidadorConversion.validarUnidad(" kilometro ", "origen", CategoriaUnidad.LONGITUD));
    }
}
