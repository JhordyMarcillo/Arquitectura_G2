package ec.edu.monster.servidor

/**
 * ÚNICA VARIABLE A CAMBIAR para conectarse desde otra red:
 * IP del equipo donde corre el servidor SOAP (celular real: IP LAN del PC; emulador: "10.0.2.2").
 */
object ConfiguracionServidor {
    const val SERVER_IP = "192.168.0.90"

    const val SERVER_PORT = "8085"
    const val URL = "http://$SERVER_IP:$SERVER_PORT/Service1.svc"
}
