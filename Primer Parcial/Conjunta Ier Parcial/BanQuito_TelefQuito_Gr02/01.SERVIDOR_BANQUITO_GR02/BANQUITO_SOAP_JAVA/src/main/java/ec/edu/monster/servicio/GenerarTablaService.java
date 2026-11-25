package ec.edu.monster.servicio;

import ec.edu.monster.dao.CreditoDAO;
import ec.edu.monster.dao.TablaDAO;
import ec.edu.monster.model.Credito;
import ec.edu.monster.model.Tabla;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenerarTablaService {
   private static final double TASA_INTERES_ANUAL = 16.5 / 100;
    private static final double TASA_INTERES_MENSUAL = TASA_INTERES_ANUAL / 12;

    public void crearCreditoYTablaAmortizacion(Credito credito) throws SQLException {
        CreditoDAO creditoDAO = new CreditoDAO();
        TablaDAO amortizacionDAO = new TablaDAO();

        int codCredito = creditoDAO.createCredito(credito);
        credito.setCodCredito(codCredito);

        List<Tabla> amortizaciones = generarTablaAmortizacion(credito);

        for (Tabla amortizacion : amortizaciones) {
            amortizacionDAO.createAmortizacion(amortizacion);
        }
    }
     public List<Tabla> consultarTablaAmortizacion(String cedula) throws SQLException {
        TablaDAO amortizacionDAO = new TablaDAO();
        return amortizacionDAO.getAmortizacionByCedula(cedula);
    }
     public List<Credito> getCreditosByCedula(String cedula) throws SQLException {
        CreditoDAO creditoDAO = new CreditoDAO();
        return creditoDAO.getCreditosByCedula(cedula);
    }

    private List<Tabla> generarTablaAmortizacion(Credito credito) {
        List<Tabla> amortizaciones = new ArrayList<>();
        double saldo = credito.getMonto();
        double valorCuota = calcularCuotaFija(credito.getMonto(), credito.getPlazoMeses(), TASA_INTERES_MENSUAL);

        // Cuota 0: fila inicial del formato del enunciado (solo saldo = valor del credito)
        Tabla inicial = new Tabla();
        inicial.setCodCredito(credito.getCodCredito());
        inicial.setCuota(0);
        inicial.setValorCuota(0);
        inicial.setInteresPagado(0);
        inicial.setCapitalPagado(0);
        inicial.setSaldo(saldo);
        amortizaciones.add(inicial);

        for (int i = 1; i <= credito.getPlazoMeses(); i++) {
            double interesPagado = saldo * TASA_INTERES_MENSUAL;
            double capitalPagado = valorCuota - interesPagado;
            saldo -= capitalPagado;

            Tabla amortizacion = new Tabla();
            amortizacion.setCodCredito(credito.getCodCredito());
            amortizacion.setCuota(i);
            amortizacion.setValorCuota(valorCuota);
            amortizacion.setInteresPagado(interesPagado);
            amortizacion.setCapitalPagado(capitalPagado);
            amortizacion.setSaldo(saldo);

            amortizaciones.add(amortizacion);
        }

        return amortizaciones;
    }

    private double calcularCuotaFija(double monto, int plazoMeses, double tasaInteresMensual) {
        return monto * (tasaInteresMensual * Math.pow(1 + tasaInteresMensual, plazoMeses)) / (Math.pow(1 + tasaInteresMensual, plazoMeses) - 1);
    }
}