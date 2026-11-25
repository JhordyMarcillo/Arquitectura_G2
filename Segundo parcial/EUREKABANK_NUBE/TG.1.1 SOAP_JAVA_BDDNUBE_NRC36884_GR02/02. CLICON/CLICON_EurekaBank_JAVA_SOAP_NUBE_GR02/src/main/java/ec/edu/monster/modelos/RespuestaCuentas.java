package ec.edu.monster.modelos;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType(name = "RespuestaCuentas", propOrder = {"cuentas"})
public class RespuestaCuentas extends Respuesta {

    private List<DatosCuenta> cuentas = new ArrayList<>();

    @XmlElementWrapper(name = "cuentas")
    @XmlElement(name = "DatosCuenta")
    public List<DatosCuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<DatosCuenta> cuentas) {
        this.cuentas = cuentas;
    }
}
