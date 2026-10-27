package ec.edu.monster.vista;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.SesionUsuario;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MENSAJE = "MENSAJE_SESION";

    private EditText etUsuario, etPassword;
    private Button btnLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuario);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        apiService = new ApiService();

        // Mensaje cuando se llega aqui por sesion expirada (CU-08 / CU-09)
        String mensaje = getIntent().getStringExtra(EXTRA_MENSAJE);
        if (mensaje != null && !mensaje.isEmpty()) {
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarLogin();
            }
        });
    }

    private void validarLogin() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("VALIDANDO...");

        // CU-01 Autenticarse: el servidor valida y entrega el token
        apiService.login(usuario, password, new ApiService.ApiCallback<String>() {
            @Override
            public void onSuccess(String token) {
                SesionUsuario.iniciar(usuario, token);
                Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("NOMBRE_USUARIO", usuario);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String codigo, String mensaje) {
                btnLogin.setEnabled(true);
                btnLogin.setText("INGRESAR");
                if (ApiService.ERROR_RED.equals(codigo)) {
                    mostrarErrorConexion(LoginActivity.this, mensaje);
                } else {
                    Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
                if (ApiService.CREDENCIALES_INCORRECTAS.equals(codigo)) {
                    etPassword.setText("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiService != null) {
            apiService.shutdown();
        }
    }

    // CU-08 / CU-09: cualquier pantalla vuelve aqui cuando el token ya no sirve
    public static boolean esErrorDeSesion(String codigo) {
        return ApiService.TOKEN_INVALIDO.equals(codigo) || ApiService.SESION_EXPIRADA.equals(codigo);
    }

    public static void volverAlLogin(Activity origen, String mensaje) {
        SesionUsuario.cerrar();
        Intent intent = new Intent(origen, LoginActivity.class);
        intent.putExtra(EXTRA_MENSAJE, mensaje);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        origen.startActivity(intent);
        origen.finish();
    }

    // Los Toast se cortan en 2 lineas; el aviso de red tiene varias
    public static void mostrarErrorConexion(Activity origen, String mensaje) {
        new AlertDialog.Builder(origen)
                .setTitle("Sin conexión")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
