package ec.edu.monster.controladores;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.servicios.IServicioEurekaBank;
import ec.edu.monster.servicios.ServicioEurekaBankRest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Estado compartido de la app: el servicio (una sesion) y la URL del servidor recordada. */
public final class SesionApp {

    /** En el emulador, 10.0.2.2 es la PC; en un celular real se escribe la IP de la maquina del servidor. */
    public static final String URL_POR_DEFECTO = ServicioEurekaBankRest.URL_POR_DEFECTO.replace("localhost", "10.0.2.2");

    public static final IServicioEurekaBank SERVICIO = new ServicioEurekaBankRest();

    private static final String PREFERENCIAS = "eurekabank";
    private static final String CLAVE_URL = "urlServidor";
    private static final ExecutorService HILOS = Executors.newFixedThreadPool(2);
    private static final Handler HILO_PRINCIPAL = new Handler(Looper.getMainLooper());

    @FunctionalInterface
    public interface Llamada<T> {
        T ejecutar() throws ExcepcionServicio;
    }

    @FunctionalInterface
    public interface Resultado<T> {
        void recibir(T valor);
    }

    private SesionApp() {
    }

    public static void cargarUrl(Context contexto) {
        SharedPreferences preferencias = contexto.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        SERVICIO.setUrlServidor(preferencias.getString(CLAVE_URL, URL_POR_DEFECTO));
    }

    public static void guardarUrl(Context contexto) {
        contexto.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE).edit()
                .putString(CLAVE_URL, SERVICIO.getUrlServidor()).apply();
    }

    /** Ejecuta la llamada en segundo plano y entrega el resultado (o el error) en el hilo de la interfaz. */
    public static <T> void ejecutar(Llamada<T> llamada, Resultado<T> alTerminar, Resultado<ExcepcionServicio> alFallar) {
        HILOS.execute(() -> {
            try {
                T valor = llamada.ejecutar();
                HILO_PRINCIPAL.post(() -> alTerminar.recibir(valor));
            } catch (ExcepcionServicio e) {
                HILO_PRINCIPAL.post(() -> alFallar.recibir(e));
            } catch (RuntimeException e) {
                HILO_PRINCIPAL.post(() -> alFallar.recibir(new ExcepcionServicio("SRV-001", "Error inesperado: " + e.getMessage(), e)));
            }
        });
    }
}
