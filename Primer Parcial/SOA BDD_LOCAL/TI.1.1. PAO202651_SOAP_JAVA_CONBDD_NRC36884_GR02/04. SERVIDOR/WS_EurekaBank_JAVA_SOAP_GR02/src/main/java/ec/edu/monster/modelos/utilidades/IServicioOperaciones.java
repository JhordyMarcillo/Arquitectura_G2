package ec.edu.monster.modelos.utilidades;

/** Operaciones transaccionales de EurekaBank (replican los procedimientos del docente). */
public interface IServicioOperaciones {

    RespuestaOperacion depositar(SolicitudDeposito solicitud, String codigoEmpleado);

    RespuestaOperacion retirar(SolicitudRetiro solicitud, String codigoEmpleado);

    RespuestaOperacion transferir(SolicitudTransferencia solicitud, String codigoEmpleado);
}
