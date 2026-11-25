package ec.edu.monster.vistas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import ec.edu.monster.R;
import ec.edu.monster.controladores.SesionApp;
import ec.edu.monster.modelos.DatosMovimiento;
import ec.edu.monster.modelos.RespuestaMovimientos;
import ec.edu.monster.utilidades.Formato;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Busqueda por cuenta, tarjeta de saldo con ingresos y salidas, e historial con colores. */
public class FragmentoMovimientos extends Fragment {

    private static final String ARGUMENTO_CUENTA = "cuenta";

    private TextInputEditText campoCuenta;
    private TextView textoError;
    private View panelResumen;
    private TextView textoCuenta;
    private TextView textoSaldo;
    private TextView textoIngresos;
    private TextView textoSalidas;
    private AdaptadorMovimientos adaptador;

    public static FragmentoMovimientos nuevo(String cuenta) {
        FragmentoMovimientos fragmento = new FragmentoMovimientos();
        Bundle argumentos = new Bundle();
        argumentos.putString(ARGUMENTO_CUENTA, cuenta);
        fragmento.setArguments(argumentos);
        return fragmento;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, ViewGroup contenedor, Bundle estado) {
        View vista = inflador.inflate(R.layout.fragmento_movimientos, contenedor, false);
        campoCuenta = vista.findViewById(R.id.campo_cuenta);
        textoError = vista.findViewById(R.id.texto_error);
        panelResumen = vista.findViewById(R.id.panel_resumen);
        textoCuenta = vista.findViewById(R.id.texto_cuenta);
        textoSaldo = vista.findViewById(R.id.texto_saldo);
        textoIngresos = vista.findViewById(R.id.texto_ingresos);
        textoSalidas = vista.findViewById(R.id.texto_salidas);

        RecyclerView lista = vista.findViewById(R.id.lista_movimientos);
        lista.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new AdaptadorMovimientos();
        lista.setAdapter(adaptador);

        vista.findViewById(R.id.boton_consultar).setOnClickListener(v -> consultar());
        campoCuenta.setOnEditorActionListener((v, accion, evento) -> {
            if (accion == EditorInfo.IME_ACTION_SEARCH) {
                consultar();
                return true;
            }
            return false;
        });

        String cuenta = getArguments() == null ? null : getArguments().getString(ARGUMENTO_CUENTA);
        if (cuenta != null) {
            campoCuenta.setText(cuenta);
            consultar();
        }
        return vista;
    }

    private void consultar() {
        String cuenta = ActividadLogin.texto(campoCuenta);
        if (!Formato.esCuenta(cuenta)) {
            mostrarError("La cuenta debe tener 8 digitos.");
            return;
        }
        ocultarTeclado();
        SesionApp.ejecutar(() -> SesionApp.SERVICIO.consultarMovimientos(cuenta), this::mostrar, error -> {
            if (isAdded() && !((ActividadPrincipal) requireActivity()).manejarSesion(error)) {
                mostrarError(error.getMessage());
            }
        });
    }

    private void mostrar(RespuestaMovimientos datos) {
        if (!isAdded()) {
            return;
        }
        String moneda = datos.getMoneda();
        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal salidas = BigDecimal.ZERO;
        for (DatosMovimiento m : datos.getMovimientos()) {
            if (Formato.esIngreso(m)) {
                ingresos = ingresos.add(m.getImporte());
            } else {
                salidas = salidas.add(m.getImporte());
            }
        }
        textoCuenta.setText("Cuenta " + datos.getCuenta() + "  ·  " + moneda);
        textoSaldo.setText(Formato.dinero(moneda, datos.getSaldo()));
        textoIngresos.setText(Formato.dinero(moneda, ingresos));
        textoSalidas.setText(Formato.dinero(moneda, salidas));

        List<DatosMovimiento> recientes = new ArrayList<>(datos.getMovimientos());
        Collections.reverse(recientes);
        adaptador.cargar(recientes, moneda);
        textoError.setVisibility(View.GONE);
        panelResumen.setVisibility(View.VISIBLE);
    }

    private void mostrarError(String mensaje) {
        textoError.setText(mensaje);
        textoError.setVisibility(View.VISIBLE);
        panelResumen.setVisibility(View.GONE);
        adaptador.cargar(new ArrayList<>(), "Soles");
    }

    private void ocultarTeclado() {
        InputMethodManager teclado = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        teclado.hideSoftInputFromWindow(campoCuenta.getWindowToken(), 0);
    }
}
