package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.SesionUsuario;

public class MainActivity extends AppCompatActivity {

    private CardView cardTemperatura, cardMasa, cardLongitud;
    private FloatingActionButton btnSalir;
    private TextView tvNombreUsuario;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sin token no se puede usar el menu (por ejemplo, si Android cerro el proceso)
        if (!SesionUsuario.estaActiva()) {
            LoginActivity.volverAlLogin(this, "Debe iniciar sesión");
            return;
        }

        // Inicializar vistas
        cardTemperatura = findViewById(R.id.cardTemperatura);
        cardMasa = findViewById(R.id.cardMasa);
        cardLongitud = findViewById(R.id.cardLongitud);
        btnSalir = findViewById(R.id.btnSalir);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);

        // Obtener nombre de usuario del intent
        String nombreUsuario = getIntent().getStringExtra("NOMBRE_USUARIO");
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            tvNombreUsuario.setText("👤 " + nombreUsuario);
        }

        // Listeners
        cardTemperatura.setOnClickListener(v -> abrirConversiones("Temperatura"));
        cardMasa.setOnClickListener(v -> abrirConversiones("Masa"));
        cardLongitud.setOnClickListener(v -> abrirConversiones("Longitud"));

        btnSalir.setOnClickListener(v -> mostrarDialogoSalir());

        apiService = new ApiService();
        cargarCategorias();
    }

    // CU-02 Consultar catalogo: solo se muestran las tarjetas que envia el servidor
    private void cargarCategorias() {
        apiService.obtenerCategorias(new ApiService.ApiCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> categorias) {
                cardTemperatura.setVisibility(categorias.contains("TEMPERATURA") ? View.VISIBLE : View.GONE);
                cardMasa.setVisibility(categorias.contains("MASA") ? View.VISIBLE : View.GONE);
                cardLongitud.setVisibility(categorias.contains("LONGITUD") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(String codigo, String mensaje) {
                if (LoginActivity.esErrorDeSesion(codigo)) {
                    LoginActivity.volverAlLogin(MainActivity.this, mensaje);
                } else if (ApiService.ERROR_RED.equals(codigo)) {
                    LoginActivity.mostrarErrorConexion(MainActivity.this, mensaje);
                } else {
                    Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void abrirConversiones(String tipo) {
        Intent intent = new Intent(MainActivity.this, TransformacionesActivity.class);
        intent.putExtra("TIPO_CONVERSION", tipo);
        startActivity(intent);
    }

    private void mostrarDialogoSalir() {
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    SesionUsuario.cerrar();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        mostrarDialogoSalir();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiService != null) {
            apiService.shutdown();
        }
    }
}
