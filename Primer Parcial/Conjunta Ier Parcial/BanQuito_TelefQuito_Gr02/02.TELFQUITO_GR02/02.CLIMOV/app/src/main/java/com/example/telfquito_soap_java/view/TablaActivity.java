package com.example.telfquito_soap_java.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.telfquito_soap_java.R;
import com.example.telfquito_soap_java.controller.CompraController;
import com.example.telfquito_soap_java.models.TablaModel;
import com.example.telfquito_soap_java.service.CompraService;

import java.util.List;

public class TablaActivity extends AppCompatActivity {

    private EditText etCedulaCliente;
    private Button btnBuscar;
    private LinearLayout tablaContainer;
    private TextView tvNoCreditos;
    private CompraController compraController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabla);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCedulaCliente = findViewById(R.id.etCedulaCliente);
        btnBuscar = findViewById(R.id.btnBuscar);
        tablaContainer = findViewById(R.id.tablaContainer);

        tvNoCreditos = new TextView(this);
        tvNoCreditos.setText("No se encontraron créditos para esta cédula");
        tvNoCreditos.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvNoCreditos.setTextSize(16);
        tvNoCreditos.setPadding(0, 32, 0, 32);
        tvNoCreditos.setVisibility(View.GONE);

        tablaContainer.addView(tvNoCreditos);

        compraController = new CompraController();

        btnBuscar.setOnClickListener(v -> buscarCreditos());
    }

    private void buscarCreditos() {
        String cedula = etCedulaCliente.getText().toString().trim();

        if (cedula.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese una cédula", Toast.LENGTH_SHORT).show();
            return;
        }

        tablaContainer.removeAllViews();
        tablaContainer.addView(tvNoCreditos);
        tvNoCreditos.setVisibility(View.GONE);

        compraController.consultarTablaAmortizacion(cedula, new CompraService.SoapCallback<List<TablaModel>>() {
            @Override
            public void onSuccess(List<TablaModel> result) {
                runOnUiThread(() -> {
                    tablaContainer.removeAllViews();

                    if (result == null || result.isEmpty()) {
                        tvNoCreditos.setVisibility(View.VISIBLE);
                        tablaContainer.addView(tvNoCreditos);
                        return;
                    }

                    for (TablaModel tabla : result) {
                        View tablaView = LayoutInflater.from(TablaActivity.this)
                                .inflate(R.layout.item_tabla, tablaContainer, false);

                        TextView tvCodCredito = tablaView.findViewById(R.id.tvCodCredito);
                        TextView tvNumCuotas = tablaView.findViewById(R.id.tvNumCuotas);
                        TextView tvValorCuota = tablaView.findViewById(R.id.tvValorCuota);
                        TextView tvInteres = tablaView.findViewById(R.id.tvInterés);
                        TextView tvCapital = tablaView.findViewById(R.id.tvCapital);
                        TextView tvSaldo = tablaView.findViewById(R.id.tvSaldo);

                        tvCodCredito.setText(String.format("Crédito ID: %d", tabla.getCodCredito()));
                        tvNumCuotas.setText(String.format("Cuota: %d", tabla.getCuota()));
                        tvValorCuota.setText(String.format("Valor: %.2f", tabla.getValorCuota()));
                        tvInteres.setText(String.format("Interés: %.2f", tabla.getInteresPagado()));
                        tvCapital.setText(String.format("Capital Pagado: %.2f", tabla.getCapitalPagado()));
                        tvSaldo.setText(String.format("Saldo Restante: %.2f", tabla.getSaldo()));

                        tablaContainer.addView(tablaView);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    tablaContainer.removeAllViews();

                    tvNoCreditos.setText("No se encontraron créditos para esta cédula");
                    tvNoCreditos.setVisibility(View.VISIBLE);
                    tablaContainer.addView(tvNoCreditos);

                    Toast.makeText(TablaActivity.this, "No se encontraron créditos para esta cédula", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}