package com.example.telfquito_soap_java.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.telfquito_soap_java.R;
import com.example.telfquito_soap_java.controller.CompraController;
import com.example.telfquito_soap_java.models.TelefonoModel;
import com.example.telfquito_soap_java.service.CompraService;

public class VenderActivity extends AppCompatActivity {

    private Spinner spinnerOperacion;
    private EditText etCedulaCliente, etMeses;
    private TextView tvMarca, tvNombre, tvPrecio, tvDescuento, tvTotal;
    private Button btnVerificarCredito, btnPagar;
    private TelefonoModel telefono;
    private CompraController compraController;
    LinearLayout layoutDiferido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerOperacion = findViewById(R.id.spinnerOperacion);
        etCedulaCliente = findViewById(R.id.etCedulaCliente);
        etMeses = findViewById(R.id.etMeses);
        tvMarca = findViewById(R.id.tvMarca);
        tvNombre = findViewById(R.id.tvNombre);
        tvPrecio = findViewById(R.id.tvPrecio);
        tvDescuento = findViewById(R.id.tvDescuento);
        tvTotal = findViewById(R.id.tvTotal);
        btnVerificarCredito = findViewById(R.id.btnVerificarCredito);
        btnPagar = findViewById(R.id.btnPagar);
        layoutDiferido = findViewById(R.id.layoutDiferido);

        compraController = new CompraController();

        telefono = (TelefonoModel) getIntent().getSerializableExtra("telefono");
        if (telefono != null) {
            tvMarca.setText(telefono.getMarca());
            tvNombre.setText(telefono.getNombre());
            tvPrecio.setText(String.valueOf(telefono.getPrecio()));
        }

        spinnerOperacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Efectivo
                    layoutDiferido.setVisibility(View.GONE);
                    tvDescuento.setText("42%");
                    double descuento = Double.parseDouble(telefono.getPrecio()) * 0.42;
                    double total = Double.parseDouble(telefono.getPrecio()) - descuento;
                    tvTotal.setText(String.valueOf(total));
                } else { // Crédito
                    layoutDiferido.setVisibility(View.VISIBLE);
                    tvDescuento.setText("0");
                    tvTotal.setText(String.valueOf(telefono.getPrecio()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnVerificarCredito.setOnClickListener(v -> {
            int plazoMeses = Integer.parseInt(etMeses.getText().toString());

            if (plazoMeses < 3 || plazoMeses > 18) {
                Toast.makeText(VenderActivity.this, "El plazo debe ser entre 3 y 18 meses", Toast.LENGTH_SHORT).show();
            }
        });

        btnPagar.setOnClickListener(v -> {
            String cedula = etCedulaCliente.getText().toString();
            if (spinnerOperacion.getSelectedItemPosition() == 0) { // Efectivo
                compraController.comprarEfectivo(telefono.getCodTelefono(), cedula, new CompraService.SoapCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("VenderActivity", "onSuccess: " + result);
                        if ("Compra en efectivo exitosa!!".equals(result)) {
                            Intent intent = new Intent(VenderActivity.this, FacturaActivity.class);
                            intent.putExtra("cedula", cedula);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VenderActivity.this, "Error: " + result, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(VenderActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else { // Crédito
                int plazoMeses = Integer.parseInt(etMeses.getText().toString());
                compraController.comprarCredito(telefono.getCodTelefono(), cedula, plazoMeses, new CompraService.SoapCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if ("Compra exitosa a credito!!".equals(result)) {
                            Intent intent = new Intent(VenderActivity.this, FacturaActivity.class);
                            intent.putExtra("cedula", cedula);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VenderActivity.this, "Error: " + result, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(VenderActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}