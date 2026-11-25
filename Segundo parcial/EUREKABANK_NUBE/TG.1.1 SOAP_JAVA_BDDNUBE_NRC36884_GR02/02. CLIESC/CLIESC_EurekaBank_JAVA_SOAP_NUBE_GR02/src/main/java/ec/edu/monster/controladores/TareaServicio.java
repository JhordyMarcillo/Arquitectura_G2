package ec.edu.monster.controladores;

import ec.edu.monster.modelos.ExcepcionServicio;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Ejecuta una llamada al servicio fuera del hilo de la interfaz y entrega el resultado en el hilo de Swing. */
public final class TareaServicio {

    @FunctionalInterface
    public interface Llamada<T> {
        T ejecutar() throws ExcepcionServicio;
    }

    private TareaServicio() {
    }

    public static <T> void ejecutar(Llamada<T> llamada, Consumer<T> alTerminar, Consumer<ExcepcionServicio> alFallar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws ExcepcionServicio {
                return llamada.ejecutar();
            }

            @Override
            protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (ExecutionException e) {
                    alFallar.accept(e.getCause() instanceof ExcepcionServicio servicio
                            ? servicio
                            : new ExcepcionServicio("SRV-001", "Error inesperado: " + e.getCause().getMessage(), e.getCause()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
