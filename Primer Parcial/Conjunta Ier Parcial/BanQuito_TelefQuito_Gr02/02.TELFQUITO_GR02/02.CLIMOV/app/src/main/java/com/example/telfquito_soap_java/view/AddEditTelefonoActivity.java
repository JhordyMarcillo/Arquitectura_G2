package com.example.telfquito_soap_java.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.telfquito_soap_java.R;
import com.example.telfquito_soap_java.controller.TelefonoController;
import com.example.telfquito_soap_java.models.TelefonoModel;
import com.example.telfquito_soap_java.service.TelefonoService;

public class AddEditTelefonoActivity extends AppCompatActivity {

    private EditText etMarca, etNombre, etPrecio;
    private Button btnSave;
    private TextView tvTitle;
    private TelefonoController telefonoController;
    private TelefonoModel telefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_telefono);

        etMarca = findViewById(R.id.etMarca);
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        btnSave = findViewById(R.id.btnSave);
        tvTitle = findViewById(R.id.tvTitle);

        telefonoController = new TelefonoController();

        telefono = (TelefonoModel) getIntent().getSerializableExtra("telefono");
        if (telefono != null) {
            etMarca.setText(telefono.getMarca());
            etNombre.setText(telefono.getNombre());
            etPrecio.setText(telefono.getPrecio());
        }

        btnSave.setOnClickListener(v -> {
            if (telefono == null) {
                telefono = new TelefonoModel();
            }
            telefono.setMarca(etMarca.getText().toString());
            telefono.setNombre(etNombre.getText().toString());
            telefono.setPrecio(etPrecio.getText().toString());
            telefono.setDisponible(1); // Set disponible to 1

            if (telefono.getCodTelefono() == 0) {
                tvTitle.setText("Agregar Teléfono");
                telefonoController.insertTelefono(telefono, new TelefonoService.SoapCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(AddEditTelefonoActivity.this, "Teléfono agregado", Toast.LENGTH_SHORT).show();
                        refreshTelefonosList();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AddEditTelefonoActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                tvTitle.setText("Editar Teléfono");
                telefonoController.updateTelefono(telefono, new TelefonoService.SoapCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(AddEditTelefonoActivity.this, "Teléfono actualizado", Toast.LENGTH_SHORT).show();
                        refreshTelefonosList();
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AddEditTelefonoActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void refreshTelefonosList() {
        Intent intent = new Intent(this, TelefonosActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}