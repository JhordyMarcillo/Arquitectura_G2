package ec.edu.monster.vista;

import ec.edu.monster.modelo.ConversionResponse;
import java.awt.Color;

/** Panel de conversiones de longitud (Monsters Inc.). */
public class PanelLongitud extends PanelConversionBase {

    public PanelLongitud() {
        super("CONVERSIONES DE LONGITUD", new Color(140, 107, 205), new String[][] {
            {"Metros → Pies", "metrosAPies"},              {"Pies → Metros", "piesAMetros"},
            {"Metros → Pulgadas", "metrosAPulgadas"},      {"Pulgadas → Metros", "pulgadasAMetros"},
            {"Kilómetros → Millas", "kilometrosAMillas"},  {"Millas → Kilómetros", "millasAKilometros"}
        });
    }

    @Override
    protected ConversionResponse ejecutar(String operacion, double valor) {
        return clienteREST.convertirLongitud(operacion, valor);
    }
}
