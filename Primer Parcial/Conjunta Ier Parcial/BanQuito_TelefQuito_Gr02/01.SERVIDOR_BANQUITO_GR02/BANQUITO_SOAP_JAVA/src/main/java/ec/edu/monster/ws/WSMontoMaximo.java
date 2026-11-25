package ec.edu.monster.ws;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import ec.edu.monster.servicio.MontoMaximoService;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebService(serviceName = "WSMontoMaximo")
public class WSMontoMaximo {

    @WebMethod(operationName = "montoMaximo")
    public double hello(@WebParam(name = "codigo") int codigo)  {
        MontoMaximoService monto = new MontoMaximoService();
        try {
            return monto.calcularMontoMaximoCredito(codigo);
        } catch (SQLException ex) {
            Logger.getLogger(WSMontoMaximo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}