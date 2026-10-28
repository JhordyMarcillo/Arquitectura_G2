package ec.edu.monster.vista;

import ec.edu.monster.modelo.ConversionResponse;
import java.awt.Color;

/** Panel de conversiones de peso/masa (Monsters Inc.). */
public class PanelPeso extends PanelConversionBase {

    public PanelPeso() {
        super("CONVERSIONES DE PESO / MASA", new Color(246, 105, 113), new String[][] {
            {"Kilogramos → Libras", "kilogramosALibras"}, {"Libras → Kilogramos", "librasAKilogramos"},
            {"Gramos → Onzas", "gramosAOnzas"},           {"Onzas → Gramos", "onzasAGramos"}
        });
    }

    @Override
    protected ConversionResponse ejecutar(String operacion, double valor) {
        return clienteREST.convertirPeso(operacion, valor);
    }
}
