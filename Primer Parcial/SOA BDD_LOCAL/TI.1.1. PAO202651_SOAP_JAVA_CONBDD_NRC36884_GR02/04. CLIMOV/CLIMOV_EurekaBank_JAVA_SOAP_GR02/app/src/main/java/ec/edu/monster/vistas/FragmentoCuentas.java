package ec.edu.monster.vistas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ec.edu.monster.R;
import ec.edu.monster.controladores.SesionApp;
import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.utilidades.Formato;
import java.math.BigDecimal;
import java.util.List;

/** Saludo, totales por moneda y tarjetas de cuentas (al tocar una se ven sus movimientos). */
public class FragmentoCuentas extends Fragment {

    private SwipeRefreshLayout refrescar;
    private TextView textoSoles;
    private TextView textoDolares;
    private TextView textoActivas;
    private TextView textoError;
    private AdaptadorCuentas adaptador;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, ViewGroup contenedor, Bundle estado) {
        View vista = inflador.inflate(R.layout.fragmento_cuentas, contenedor, false);
        refrescar = vista.findViewById(R.id.refrescar);
        textoSoles = vista.findViewById(R.id.texto_soles);
        textoDolares = vista.findViewById(R.id.texto_dolares);
        textoActivas = vista.findViewById(R.id.texto_activas);
        textoError = vista.findViewById(R.id.texto_error);
        ((TextView) vista.findViewById(R.id.texto_empleado)).setText(SesionApp.SERVICIO.getSesion().getNombreEmpleado());

        RecyclerView lista = vista.findViewById(R.id.lista_cuentas);
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new AdaptadorCuentas(cuenta -> ((ActividadPrincipal) requireActivity()).verMovimientos(cuenta));
        lista.setAdapter(adaptador);

        refrescar.setColorSchemeResources(R.color.primario);
        refrescar.setOnRefreshListener(this::cargar);
        vista.findViewById(R.id.boton_salir).setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesion")
                .setMessage("¿Desea salir de EurekaBank?")
                .setPositiveButton("Salir", (d, w) -> ((ActividadPrincipal) requireActivity()).volverAlLogin(null))
                .setNegativeButton("Cancelar", null)
                .show());
        cargar();
        return vista;
    }

    private void cargar() {
        SesionApp.ejecutar(() -> SesionApp.SERVICIO.listarCuentas().getCuentas(), this::mostrar, error -> {
            refrescar.setRefreshing(false);
            if (!isAdded() || ((ActividadPrincipal) requireActivity()).manejarSesion(error)) {
                return;
            }
            textoError.setText(error.getMessage());
            textoError.setVisibility(View.VISIBLE);
        });
    }

    private void mostrar(List<DatosCuenta> cuentas) {
        refrescar.setRefreshing(false);
        if (!isAdded()) {
            return;
        }
        BigDecimal soles = BigDecimal.ZERO;
        BigDecimal dolares = BigDecimal.ZERO;
        int activas = 0;
        for (DatosCuenta c : cuentas) {
            if (Formato.esActiva(c)) {
                activas++;
                if ("S/".equals(Formato.simbolo(c.getMoneda()))) {
                    soles = soles.add(c.getSaldo());
                } else {
                    dolares = dolares.add(c.getSaldo());
                }
            }
        }
        textoSoles.setText(Formato.dinero("Soles", soles));
        textoDolares.setText(Formato.dinero("Dolares", dolares));
        textoActivas.setText(activas + " de " + cuentas.size() + " activas");
        textoError.setVisibility(View.GONE);
        adaptador.cargar(cuentas);
    }
}
