package ec.edu.monster.servicios;

import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.RespuestaAutenticacion;
import ec.edu.monster.modelos.RespuestaCuentas;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.modelos.RespuestaOperacion;
import java.math.BigDecimal;

/** Operaciones del contrato EurekaBank que consumen los clientes (ver CONTRATO_EUREKABANK_GR02.md). */
public interface IServicioEurekaBank {

    String getUrlServidor();

    void setUrlServidor(String url);

    RespuestaAutenticacion getSesion();

    RespuestaAutenticacion iniciarSesion(String usuario, String clave) throws ExcepcionServicio;

    void cerrarSesion();

    /** Retoma una sesion guardada (por ejemplo, en la sesion HTTP del cliente web). */
    void restaurarSesion(RespuestaAutenticacion sesion);

    RespuestaCuentas listarCuentas() throws ExcepcionServicio;

    RespuestaMovimientos consultarMovimientos(String cuenta) throws ExcepcionServicio;

    RespuestaOperacion depositar(String cuenta, BigDecimal importe) throws ExcepcionServicio;

    RespuestaOperacion retirar(String cuenta, String clave, BigDecimal importe) throws ExcepcionServicio;

    RespuestaOperacion transferir(String cuentaOrigen, String claveOrigen, String cuentaDestino, BigDecimal importe)
            throws ExcepcionServicio;
}
