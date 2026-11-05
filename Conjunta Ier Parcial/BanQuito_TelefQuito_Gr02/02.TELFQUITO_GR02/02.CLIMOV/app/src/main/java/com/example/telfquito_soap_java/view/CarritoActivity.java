package com.example.telfquito_soap_java.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telfquito_soap_java.controller.TelefonoController;
import com.example.telfquito_soap_java.models.FacturaModel;
import com.example.telfquito_soap_java.models.TelefonoModel;
import com.example.telfquito_soap_java.service.CompraService;
import com.example.telfquito_soap_java.service.TelefonoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Compra de 1 a 3 telefonos en una sola factura (efectivo con 42% de descuento o credito directo).
 */
public class CarritoActivity extends AppCompatActivity {

    private static final int MAX_PRODUCTOS = 3;

    private final List<TelefonoModel> catalogo = new ArrayList<>();
    private final List<CheckBox> casillas = new ArrayList<>();
    private TextView tvTotal;
    private RadioButton rbCredito;
    private EditText etCedula;
    private EditText etPlazo;
    private LinearLayout listaTelefonos;
    private final CompraService compraService = new CompraService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(0xFFF2F4F8);

        LinearLayout raiz = new LinearLayout(this);
        raiz.setOrientation(LinearLayout.VERTICAL);
        int p = dp(16);
        raiz.setPadding(p, dp(32), p, p);
        scroll.addView(raiz);

        raiz.addView(texto("Compra con factura", 22, true));
        raiz.addView(texto("Seleccione de 1 a " + MAX_PRODUCTOS + " teléfonos", 14, false));

        listaTelefonos = new LinearLayout(this);
        listaTelefonos.setOrientation(LinearLayout.VERTICAL);
        listaTelefonos.setPadding(0, dp(8), 0, dp(8));
        raiz.addView(listaTelefonos);

        tvTotal = texto("Total: $0.00", 18, true);
        raiz.addView(tvTotal);

        raiz.addView(texto("Forma de pago", 14, true));
        RadioGroup grupo = new RadioGroup(this);
        grupo.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton rbEfectivo = new RadioButton(this);
        rbEfectivo.setText("Efectivo (42% dto.)");
        rbEfectivo.setId(View.generateViewId());
        rbCredito = new RadioButton(this);
        rbCredito.setText("Crédito directo");
        rbCredito.setId(View.generateViewId());
        grupo.addView(rbEfectivo);
        grupo.addView(rbCredito);
        grupo.check(rbEfectivo.getId());
        raiz.addView(grupo);

        etCedula = campo("Cédula del cliente", InputType.TYPE_CLASS_NUMBER);
        raiz.addView(etCedula);
        etPlazo = campo("Plazo en meses (3 a 18)", InputType.TYPE_CLASS_NUMBER);
        etPlazo.setVisibility(View.GONE);
        raiz.addView(etPlazo);
        grupo.setOnCheckedChangeListener((g, id) -> {
            etPlazo.setVisibility(id == rbCredito.getId() ? View.VISIBLE : View.GONE);
            actualizarTotal();
        });

        Button comprar = new Button(this);
        comprar.setText("Comprar y generar factura");
        comprar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF198754));
        comprar.setTextColor(0xFFFFFFFF);
        comprar.setOnClickListener(v -> comprar());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(12);
        raiz.addView(comprar, lp);

        setContentView(scroll);
        cargarCatalogo();
    }

    private void cargarCatalogo() {
        new TelefonoController().getAllTelefonos(new TelefonoService.SoapCallback<List<TelefonoModel>>() {
            @Override
            public void onSuccess(List<TelefonoModel> result) {
                catalogo.clear();
                casillas.clear();
                listaTelefonos.removeAllViews();
                for (TelefonoModel t : result) {
                    if (t.getDisponible() != 1) {
                        continue;
                    }
                    catalogo.add(t);
                    CheckBox cb = new CheckBox(CarritoActivity.this);
                    cb.setText(String.format(Locale.US, "%s  (%s)  $%.2f", t.getNombre(), t.getMarca(), precio(t)));
                    cb.setOnCheckedChangeListener((b, marcado) -> {
                        if (marcado && seleccionados().size() > MAX_PRODUCTOS) {
                            b.setChecked(false);
                            Toast.makeText(CarritoActivity.this,
                                    "Máximo " + MAX_PRODUCTOS + " productos por factura", Toast.LENGTH_SHORT).show();
                        }
                        actualizarTotal();
                    });
                    casillas.add(cb);
                    listaTelefonos.addView(cb);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(CarritoActivity.this, "No se pudo cargar el catálogo: " + errorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<TelefonoModel> seleccionados() {
        List<TelefonoModel> sel = new ArrayList<>();
        for (int i = 0; i < casillas.size(); i++) {
            if (casillas.get(i).isChecked()) {
                sel.add(catalogo.get(i));
            }
        }
        return sel;
    }

    private void actualizarTotal() {
        double subtotal = 0;
        for (TelefonoModel t : seleccionados()) {
            subtotal += precio(t);
        }
        if (rbCredito.isChecked()) {
            tvTotal.setText(String.format(Locale.US, "Total: $%.2f", subtotal));
        } else {
            double desc = subtotal * 0.42;
            tvTotal.setText(String.format(Locale.US, "Subtotal $%.2f  -  Descuento $%.2f\nTotal: $%.2f",
                    subtotal, desc, subtotal - desc));
        }
    }

    private void comprar() {
        List<TelefonoModel> sel = seleccionados();
        String cedula = etCedula.getText().toString().trim();
        if (sel.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un teléfono", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingrese la cédula", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean credito = rbCredito.isChecked();
        int plazo = 0;
        if (credito) {
            try {
                plazo = Integer.parseInt(etPlazo.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingrese el plazo en meses (3 a 18)", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        StringBuilder cods = new StringBuilder();
        for (TelefonoModel t : sel) {
            cods.append(cods.length() == 0 ? "" : ",").append(t.getCodTelefono());
        }
        compraService.comprarVarios(credito, cods.toString(), cedula, plazo, new CompraService.SoapCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null && result.toLowerCase(Locale.ROOT).contains("factura no.")) {
                    mostrarFactura(result, cedula);
                } else {
                    dialogo("No se realizó la compra", result);
                }
            }

            @Override
            public void onError(String errorMessage) {
                dialogo("Error de conexión", errorMessage);
            }
        });
    }

    /** Busca las lineas de la ultima factura del cliente y las muestra. */
    private void mostrarFactura(final String mensaje, String cedula) {
        compraService.obtenerFactura(cedula, new CompraService.SoapCallback<List<FacturaModel>>() {
            @Override
            public void onSuccess(List<FacturaModel> filas) {
                int ultima = 0;
                for (FacturaModel f : filas) {
                    ultima = Math.max(ultima, f.getCodCompra());
                }
                StringBuilder sb = new StringBuilder();
                double subtotal = 0, descuento = 0, total = 0;
                String cliente = "", fecha = "", pago = "";
                for (FacturaModel f : filas) {
                    if (f.getCodCompra() != ultima) {
                        continue;
                    }
                    cliente = f.getNombreCliente();
                    fecha = f.getFecha();
                    pago = f.getFormaPago();
                    sb.append(String.format(Locale.US, "• %s (%s)  $%.2f\n", f.getNombreTelefono(),
                            f.getMarcaTelefono(), f.getPreciofinal() + f.getDescuento()));
                    subtotal += f.getPreciofinal() + f.getDescuento();
                    descuento += f.getDescuento();
                    total += f.getPreciofinal();
                }
                String cabecera = String.format(Locale.US, "FACTURA No. %d\nFecha: %s\nCliente: %s\nPago: %s\n\n",
                        ultima, fecha, cliente, pago);
                String pie = String.format(Locale.US, "\nSubtotal: $%.2f\nDescuento: $%.2f\nTOTAL: $%.2f",
                        subtotal, descuento, total);
                dialogo("Compra exitosa", cabecera + sb + pie);
            }

            @Override
            public void onError(String errorMessage) {
                dialogo("Compra exitosa", mensaje);
            }
        });
    }

    private void dialogo(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (d, w) -> {
                    if (titulo.startsWith("Compra exitosa")) {
                        finish();
                    }
                })
                .show();
    }

    private static double precio(TelefonoModel t) {
        try {
            return Double.parseDouble(t.getPrecio());
        } catch (Exception e) {
            return 0;
        }
    }

    private TextView texto(String s, int sp, boolean negrita) {
        TextView tv = new TextView(this);
        tv.setText(s);
        tv.setTextSize(sp);
        tv.setTextColor(0xFF1B2A41);
        tv.setPadding(0, dp(4), 0, dp(4));
        if (negrita) {
            tv.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return tv;
    }

    private EditText campo(String hint, int tipo) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setInputType(tipo);
        et.setGravity(Gravity.START);
        return et;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
