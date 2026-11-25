import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;
import ec.edu.monster.modelos.utilidades.ConversorLongitud;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConversorLongitudTest {

    private final ConversorLongitud conversor = new ConversorLongitud();

    @ParameterizedTest
    @CsvSource({
        "5, KILOMETRO, METRO, 5000.0",
        "1, MILLA, KILOMETRO, 1.609344",
        "10, PIE, METRO, 3.048"
    })
    void deberiaConvertirCorrectamente(double valor,
                                       UnidadLongitud origen,
                                       UnidadLongitud destino,
                                       double esperado) {

        double resultado = conversor.convertir(valor, origen, destino);

        assertEquals(esperado, resultado, 0.0001);
    }
}
