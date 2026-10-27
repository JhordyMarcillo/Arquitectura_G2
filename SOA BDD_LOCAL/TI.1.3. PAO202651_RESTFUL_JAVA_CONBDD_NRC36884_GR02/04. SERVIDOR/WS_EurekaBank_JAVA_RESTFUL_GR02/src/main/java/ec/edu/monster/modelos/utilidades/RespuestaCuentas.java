package ec.edu.monster.modelos.utilidades;

import jakarta.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonbPropertyOrder({"cuentas"})
public class RespuestaCuentas extends Respuesta {

    private List<DatosCuenta> cuentas = new ArrayList<>();

    public List<DatosCuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<DatosCuenta> cuentas) {
        this.cuentas = cuentas;
    }
}
