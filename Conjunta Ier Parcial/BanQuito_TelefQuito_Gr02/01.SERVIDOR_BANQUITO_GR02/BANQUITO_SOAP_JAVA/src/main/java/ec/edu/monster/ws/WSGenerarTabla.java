package ec.edu.monster.ws;

import ec.edu.monster.model.Credito;
import ec.edu.monster.model.Tabla;
import ec.edu.monster.servicio.GenerarTablaService;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebService(serviceName = "WSGenerarTabla")
public class WSGenerarTabla {
/**
     * Web service operation to create credit and generate amortization table.
     * @param codCliente
     * @param monto
     * @param plazoMeses
     * @return 
     */
    @WebMethod(operationName = "crearCreditoYTablaAmortizacion")
    public String crearCreditoYTablaAmortizacion(
            @WebParam(name = "codCliente") int codCliente,
            @WebParam(name = "monto") double monto,
            @WebParam(name = "plazoMeses") int plazoMeses) {
        
        if (plazoMeses < 3 || plazoMeses > 18) {
            return "El plazo del crédito debe ser mayor o igual a 3 meses y menor o igual a 18 meses.";
        }

        Credito credito = new Credito();
        credito.setCodCliente(codCliente);
        credito.setMonto(monto);
        credito.setPlazoMeses(plazoMeses);
        credito.setTasaInteres(16.5); 
        credito.setFechaInicio(LocalDate.now().toString());

        GenerarTablaService generarTablaService = new GenerarTablaService();
        try {
            generarTablaService.crearCreditoYTablaAmortizacion(credito);
            return "Crédito y tabla de amortización creados exitosamente.";
        } catch (SQLException e) {
            return "Error al crear el crédito y la tabla de amortización: " + e.getMessage();
        }
    }
    /**
     * Web service operation to get amortization table for a given loan ID.
     * @param cedula
     * @return 
     */
    @WebMethod(operationName = "consultarTablaAmortizacion")
    public List<Tabla> consultarTablaAmortizacion(@WebParam(name = "cedula") String cedula) {
        GenerarTablaService generarTablaService = new GenerarTablaService();
        try {
            return generarTablaService.consultarTablaAmortizacion(cedula);
        } catch (SQLException e) {
            return null;
        }
    }
     /**
     * Web service operation to get credits for a given client ID.
     * @param cedula
     * @return 
     */
    @WebMethod(operationName = "consultarCreditosPorCedula")
    public List<Credito> consultarCreditosPorCedula(@WebParam(name = "cedula") String cedula) {
        GenerarTablaService generarTablaService = new GenerarTablaService();
        try {
            return generarTablaService.getCreditosByCedula(cedula);
        } catch (SQLException e) {
            return null;
        }
    }
}