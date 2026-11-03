package ec.edu.monster.servicio;

import ec.edu.monster.dao.MovimientoDAO;
import java.sql.SQLException;

public class MontoMaximoService {
    MovimientoDAO movimientoDAO = new MovimientoDAO();
    
    public double calcularMontoMaximoCredito(int cod) throws SQLException {
        double promedioDepositos = movimientoDAO.obtenerPromedioDepositos(cod);
        double promedioRetiros = movimientoDAO.obtenerPromedioRetiros(cod);

        double diferenciaPromedios = promedioDepositos - promedioRetiros;
        double montoMaximoCredito = (diferenciaPromedios * 0.30) * 6; // ((Prom.Dep - Prom.Ret) * 30%) * 6

        return Math.max(0, montoMaximoCredito);
    }
}
