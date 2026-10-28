package ec.edu.monster.utilidades;

import java.util.Random;

/**
 * Utilidades para generar mensajes de error monstruosos y divertidos
 * @author ACER NITRO V15
 */
public class MensajesErrorMonstruosos {
    
    private static final Random random = new Random();
    
    // Mensajes de error monstruosos para conexión
    private static final String[] MENSAJES_CONEXION = {
        "¡GRRRR! Un error monstruoso ha aparecido: El servidor se ha escondido y no podemos encontrarlo. ¡Espera hasta que lo solucionemos!",
        "¡ROAR! El monstruo del servidor está durmiendo. ¡Despiértalo y vuelve a intentar!",
        "¡BOOM! El servidor ha sido devorado por un monstruo hambriento. ¡Espera a que lo regurgite!",
        "¡CRASH! El servidor se ha transformado en un monstruo y huyó. ¡Búscalo y tráelo de vuelta!",
        "¡ZAP! Un rayo monstruoso ha derribado el servidor. ¡Espera a que se recupere!",
        "¡POOF! El servidor se ha vuelto invisible. ¡Usa tus poderes monstruosos para encontrarlo!",
        "¡BANG! El servidor está atrapado en una dimensión monstruosa. ¡Espera a que escape!",
        "¡SNAP! El servidor se ha roto como un juguete monstruoso. ¡Espera a que lo arreglen!",
        "¡WOOSH! El servidor ha sido succionado por un agujero negro monstruoso. ¡Espera a que regrese!",
        "¡THUD! El servidor se ha caído y está inconsciente. ¡Espera a que despierte!"
    };
    
    // Mensajes de error monstruosos para servidor
    private static final String[] MENSAJES_SERVIDOR = {
        "¡GRRRR! El servidor está confundido y no sabe qué hacer. ¡Espera a que se calme!",
        "¡ROAR! El servidor está de mal humor. ¡Espera a que mejore su actitud!",
        "¡BOOM! El servidor está sobrecargado de trabajo. ¡Espera a que termine!",
        "¡CRASH! El servidor está teniendo un mal día. ¡Espera a que mejore!",
        "¡ZAP! El servidor está siendo atacado por monstruos. ¡Espera a que se defienda!",
        "¡POOF! El servidor está haciendo trucos de magia. ¡Espera a que termine!",
        "¡BANG! El servidor está construyendo algo monstruoso. ¡Espera a que termine!",
        "¡SNAP! El servidor está rompiendo cosas. ¡Espera a que se calme!",
        "¡WOOSH! El servidor está corriendo muy rápido. ¡Espera a que pare!",
        "¡THUD! El servidor se ha caído. ¡Espera a que se levante!"
    };
    
    /**
     * Genera un mensaje de error monstruoso para problemas de conexión
     * @param excepcion La excepción original
     * @return Mensaje de error monstruoso
     */
    public static String generarMensajeErrorConexion(Exception excepcion) {
        String mensajeBase = MENSAJES_CONEXION[random.nextInt(MENSAJES_CONEXION.length)];
        
        // Agregar información técnica si es útil
        if (excepcion.getMessage() != null && excepcion.getMessage().contains("Connection refused")) {
            mensajeBase += "\n\n🔧 Información técnica: El servidor no está respondiendo. Verifica que esté ejecutándose.";
        } else if (excepcion.getMessage() != null && excepcion.getMessage().contains("timeout")) {
            mensajeBase += "\n\n🔧 Información técnica: El servidor tardó demasiado en responder.";
        } else if (excepcion.getMessage() != null && excepcion.getMessage().contains("UnknownHostException")) {
            mensajeBase += "\n\n🔧 Información técnica: No se puede encontrar el servidor.";
        }
        
        return mensajeBase;
    }
    
    /**
     * Genera un mensaje de error monstruoso para problemas del servidor
     * @param codigoError El código de error HTTP
     * @return Mensaje de error monstruoso
     */
    public static String generarMensajeErrorServidor(int codigoError) {
        String mensajeBase = MENSAJES_SERVIDOR[random.nextInt(MENSAJES_SERVIDOR.length)];
        
        // Agregar información específica según el código de error
        switch (codigoError) {
            case 400:
                mensajeBase += "\n\n🔧 Información técnica: El servidor no entiende tu petición (Error 400).";
                break;
            case 401:
                mensajeBase += "\n\n🔧 Información técnica: No tienes permiso para acceder (Error 401).";
                break;
            case 403:
                mensajeBase += "\n\n🔧 Información técnica: El acceso está prohibido (Error 403).";
                break;
            case 404:
                mensajeBase += "\n\n🔧 Información técnica: El recurso no existe (Error 404).";
                break;
            case 500:
                mensajeBase += "\n\n🔧 Información técnica: Error interno del servidor (Error 500).";
                break;
            case 503:
                mensajeBase += "\n\n🔧 Información técnica: El servidor está sobrecargado (Error 503).";
                break;
            default:
                mensajeBase += "\n\n🔧 Información técnica: Error del servidor (Código " + codigoError + ").";
                break;
        }
        
        return mensajeBase;
    }
    
    /**
     * Genera un mensaje de error genérico monstruoso
     * @param mensajeOriginal El mensaje de error original
     * @return Mensaje de error monstruoso
     */
    public static String generarMensajeErrorGenerico(String mensajeOriginal) {
        String mensajeBase = MENSAJES_CONEXION[random.nextInt(MENSAJES_CONEXION.length)];
        mensajeBase += "\n\n🔧 Información técnica: " + mensajeOriginal;
        return mensajeBase;
    }
    
    /**
     * Genera un mensaje de bienvenida monstruoso para errores
     * @return Mensaje de bienvenida monstruoso
     */
    public static String generarBienvenidaError() {
        return "╔══════════════════════════════════════════════════════════════════════════════╗\n" +
               "║                        ¡ERROR MONSTRUOSO DETECTADO!                          ║\n" +
               "╚══════════════════════════════════════════════════════════════════════════════╝";
    }
    
    /**
     * Genera un mensaje de despedida monstruoso para errores
     * @return Mensaje de despedida monstruoso
     */
    public static String generarDespedidaError() {
        return "╔══════════════════════════════════════════════════════════════════════════════╗\n" +
               "║                    ¡ESPERA A QUE EL MONSTRUO SE CALME!                      ║\n" +
               "╚══════════════════════════════════════════════════════════════════════════════╝";
    }
}


