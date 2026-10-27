package ec.edu.monster.vistas;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import ec.edu.monster.R;
import ec.edu.monster.controladores.SesionApp;
import ec.edu.monster.modelos.ExcepcionServicio;
import ec.edu.monster.modelos.RespuestaOperacion;
import ec.edu.monster.utilidades.Formato;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Deposito, retiro y transferencia con selector segmentado y comprobante. */
public class FragmentoOperaciones extends Fragment {

    private enum Tipo { DEPOSITO, RETIRO, TRANSFERENCIA }

    private Tipo tipo = Tipo.DEPOSITO;
    private View vista;
    private TextInputEditText campoCuenta;
    private TextInputEditText campoClave;
    private TextInputEditText campoDestino;
    private TextInputEditText campoImporte;
    private MaterialButton botonRegistrar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, ViewGroup contenedor, Bundle estado) {
        vista = inflador.inflate(R.layout.fragmento_operaciones, contenedor, false);
        campoCuenta = vista.findViewById(R.id.campo_cuenta);
        campoClave = vista.findViewById(R.id.campo_clave);
        campoDestino = vista.findViewById(R.id.campo_destino);
        campoImporte = vista.findViewById(R.id.campo_importe);
        botonRegistrar = vista.findViewById(R.id.boton_registrar);

        MaterialButtonToggleGroup selector = vista.findViewById(R.id.selector);
        selector.addOnButtonCheckedListener((grupo, boton, marcado) -> {
            if (marcado) {
                seleccionar(boton == R.id.opcion_retiro ? Tipo.RETIRO
                        : boton == R.id.opcion_transferencia ? Tipo.TRANSFERENCIA : Tipo.DEPOSITO);
            }
        });
        botonRegistrar.setOnClickListener(v -> registrar());
        seleccionar(Tipo.DEPOSITO);
        return vista;
    }

    private void seleccionar(Tipo nuevo) {
        tipo = nuevo;
        ((TextView) vista.findViewById(R.id.texto_descripcion)).setText(switch (tipo) {
            case DEPOSITO -> "Abono de efectivo a una cuenta activa.";
            case RETIRO -> "Retiro validado con la clave y el saldo disponible.";
            default -> "Traspaso entre cuentas de la misma moneda.";
        });
        ((TextInputLayout) vista.findViewById(R.id.marco_cuenta)).setHint(tipo == Tipo.TRANSFERENCIA ? "Cuenta de origen" : "Cuenta");
        vista.findViewById(R.id.marco_clave).setVisibility(tipo == Tipo.DEPOSITO ? View.GONE : View.VISIBLE);
        vista.findViewById(R.id.marco_destino).setVisibility(tipo == Tipo.TRANSFERENCIA ? View.VISIBLE : View.GONE);
        ((TextView) vista.findViewById(R.id.texto_nota)).setText(tipo == Tipo.TRANSFERENCIA
                ? "Cada cuenta paga el costo por movimiento: S/ 2.00 en soles y US$ 0.60 en dolares."
                : "Se aplica el costo por movimiento: S/ 2.00 en soles y US$ 0.60 en dolares.");
        botonRegistrar.setText("Registrar " + tipo.name().toLowerCase(Locale.ROOT));
        vista.findViewById(R.id.panel_comprobante).setVisibility(View.GONE);
    }

    private void registrar() {
        String cuenta = ActividadLogin.texto(campoCuenta);
        String clave = ActividadLogin.texto(campoClave);
        String destino = ActividadLogin.texto(campoDestino);
        BigDecimal importe = Formato.leerImporte(ActividadLogin.texto(campoImporte));
        String error = !Formato.esCuenta(cuenta) ? "La cuenta debe tener 8 digitos."
                : tipo != Tipo.DEPOSITO && clave.isEmpty() ? "Ingrese la clave de la cuenta."
                : tipo == Tipo.TRANSFERENCIA && !Formato.esCuenta(destino) ? "La cuenta de destino debe tener 8 digitos."
                : importe == null ? "Ingrese un importe mayor a cero (ej. 150.50)." : null;
        if (error != null) {
            comprobante(false, "Revise los datos", error, new String[0][]);
            return;
        }

        botonRegistrar.setEnabled(false);
        SesionApp.ejecutar(() -> {
            RespuestaOperacion r = switch (tipo) {
                case DEPOSITO -> SesionApp.SERVICIO.depositar(cuenta, importe);
                case RETIRO -> SesionApp.SERVICIO.retirar(cuenta, clave, importe);
                default -> SesionApp.SERVICIO.transferir(cuenta, clave, destino, importe);
            };
            return new Object[]{r, SesionApp.SERVICIO.consultarMovimientos(r.getCuenta()).getMoneda()};
        }, resultado -> {
            botonRegistrar.setEnabled(true);
            RespuestaOperacion r = (RespuestaOperacion) resultado[0];
            String moneda = (String) resultado[1];
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).format(new Date());
            String[][] lineas = tipo == Tipo.TRANSFERENCIA
                    ? new String[][]{{"Operacion", "TRANSFERENCIA"}, {"Origen", r.getCuenta()}, {"Destino", destino},
                        {"Importe", Formato.dinero(moneda, importe)}, {"Nuevo saldo", Formato.dinero(moneda, r.getSaldo())}, {"Fecha y hora", fecha}}
                    : new String[][]{{"Operacion", tipo.name()}, {"Cuenta", r.getCuenta()}, {"Importe", Formato.dinero(moneda, importe)},
                        {"Nuevo saldo", Formato.dinero(moneda, r.getSaldo())}, {"Fecha y hora", fecha}};
            comprobante(true, "Operacion exitosa", r.getMensaje(), lineas);
            campoImporte.setText("");
            campoClave.setText("");
        }, (ExcepcionServicio e) -> {
            botonRegistrar.setEnabled(true);
            if (isAdded() && !((ActividadPrincipal) requireActivity()).manejarSesion(e)) {
                comprobante(false, "Operacion rechazada", e.getMessage(), new String[][]{{"Codigo", e.getCodigo()}});
            }
        });
    }

    private void comprobante(boolean exito, String titulo, String mensaje, String[][] lineas) {
        if (!isAdded()) {
            return;
        }
        ((ImageView) vista.findViewById(R.id.icono_resultado)).setImageResource(exito ? R.drawable.ic_exito : R.drawable.ic_error);
        ((TextView) vista.findViewById(R.id.texto_resultado)).setText(titulo);
        ((TextView) vista.findViewById(R.id.texto_mensaje)).setText(mensaje);

        LinearLayout detalle = vista.findViewById(R.id.lista_detalle);
        detalle.removeAllViews();
        for (String[] linea : lineas) {
            boolean destacado = "Nuevo saldo".equals(linea[0]);
            LinearLayout fila = new LinearLayout(requireContext());
            fila.setPadding(0, 10, 0, 10);
            TextView etiqueta = new TextView(requireContext());
            etiqueta.setText(linea[0]);
            etiqueta.setTextColor(ContextCompat.getColor(requireContext(), R.color.texto_secundario));
            fila.addView(etiqueta, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            TextView valor = new TextView(requireContext());
            valor.setText(linea[1]);
            valor.setTypeface(Typeface.DEFAULT_BOLD);
            valor.setTextSize(destacado ? 18 : 14);
            valor.setTextColor(ContextCompat.getColor(requireContext(), destacado ? R.color.primario : R.color.texto));
            fila.addView(valor);
            detalle.addView(fila);
        }
        View panel = vista.findViewById(R.id.panel_comprobante);
        panel.setVisibility(View.VISIBLE);
        NestedScrollView desplazamiento = vista.findViewById(R.id.desplazamiento);
        desplazamiento.post(() -> desplazamiento.smoothScrollTo(0, panel.getBottom()));
    }
}
