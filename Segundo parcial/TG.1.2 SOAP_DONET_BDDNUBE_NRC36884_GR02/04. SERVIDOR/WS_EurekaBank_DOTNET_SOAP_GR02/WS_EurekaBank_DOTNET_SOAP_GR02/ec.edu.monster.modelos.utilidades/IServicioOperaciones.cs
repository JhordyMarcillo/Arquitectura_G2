namespace ec.edu.monster.modelos.utilidades
{
    /// <summary>Operaciones transaccionales de EurekaBank (replican los procedimientos del docente).</summary>
    public interface IServicioOperaciones
    {
        RespuestaOperacion Depositar(SolicitudDeposito solicitud, string codigoEmpleado);
        RespuestaOperacion Retirar(SolicitudRetiro solicitud, string codigoEmpleado);
        RespuestaOperacion Transferir(SolicitudTransferencia solicitud, string codigoEmpleado);
    }
}
