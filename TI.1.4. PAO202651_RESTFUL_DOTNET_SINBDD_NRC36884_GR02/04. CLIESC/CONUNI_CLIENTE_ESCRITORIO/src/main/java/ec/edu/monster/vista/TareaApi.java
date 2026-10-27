package ec.edu.monster.vista;

import ec.edu.monster.modelo.ExcepcionApi;

import java.awt.Component;
import java.awt.Cursor;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

// Ejecuta una llamada al servidor fuera del hilo de Swing para que la ventana no se congele
public final class TareaApi {

    public interface Accion<T> {
        T ejecutar() throws ExcepcionApi;
    }

    private TareaApi() {
    }

    public static <T> void ejecutar(Component ventana, Accion<T> accion,
                                    Consumer<T> alTerminar, Consumer<ExcepcionApi> alFallar) {
        ventana.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return accion.ejecutar();
            }

            @Override
            protected void done() {
                ventana.setCursor(Cursor.getDefaultCursor());
                T resultado;
                try {
                    resultado = get();
                } catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    alFallar.accept(causa instanceof ExcepcionApi
                            ? (ExcepcionApi) causa
                            : new ExcepcionApi("RED", "Error: " + causa.getMessage()));
                    return;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                alTerminar.accept(resultado);
            }
        }.execute();
    }
}
