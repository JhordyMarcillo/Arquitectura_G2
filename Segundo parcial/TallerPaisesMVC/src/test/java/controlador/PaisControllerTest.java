package controlador;

import ec.edu.monster.controlador.PaisController;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import ec.edu.monster.modelo.Pais;
import org.junit.jupiter.api.Test;

class PaisControllerTest {

    // Forma real de la respuesta de REST Countries v5
    private static final String JSON = """
        {"data":{"objects":[
          {"names":{"common":"Canada"},
           "capitals":[{"attributes":{"primary":true},"name":"Ottawa"}],
           "region":"Americas","population":41798407,
           "currencies":[{"code":"CAD","name":"Canadian dollar","symbol":"$"}]},
          {"names":{"common":"Sinmonedalandia"},"region":"Europe","population":5}
        ],"meta":{"total":2}}}
        """;

    @Test
    void convierteJsonEnPaises() {
        JsonObject resp = JsonParser.parseString(JSON).getAsJsonObject();
        List<Pais> l = PaisController.parsear(resp);
        assertEquals(2, l.size());
        Pais c = l.get(0);
        assertEquals("Canada", c.getNombre());
        assertEquals("Ottawa", c.getCapital());
        assertEquals("Americas", c.getRegion());
        assertEquals(41798407L, c.getPoblacion());
        assertEquals("Canadian dollar (CAD)", c.getMoneda());
        assertEquals(2, PaisController.totalDe(resp));
    }

    @Test
    void toleraCamposAusentes() {
        Pais p = PaisController.parsear(JsonParser.parseString(JSON).getAsJsonObject()).get(1);
        assertEquals("", p.getCapital());
        assertEquals("", p.getMoneda());
    }

    @Test
    void respuestaSinDatosDaListaVacia() {
        assertTrue(PaisController.parsear(JsonParser.parseString("{\"data\":null}")
                .getAsJsonObject()).isEmpty());
    }
}
