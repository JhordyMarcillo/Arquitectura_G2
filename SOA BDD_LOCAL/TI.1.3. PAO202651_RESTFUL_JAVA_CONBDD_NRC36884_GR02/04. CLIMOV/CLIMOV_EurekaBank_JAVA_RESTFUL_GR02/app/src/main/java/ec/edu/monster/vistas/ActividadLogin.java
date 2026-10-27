package ec.edu.monster.vistas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import ec.edu.monster.R;
import ec.edu.monster.controladores.SesionApp;

/** Inicio de sesion con encabezado de marca y formulario en tarjeta. */
public class ActividadLogin extends AppCompatActivity {

    public static final String EXTRA_AVISO = "aviso";

    private TextInputEditText campoUsuario;
    private TextInputEditText campoClave;
    private TextInputEditText campoServidor;
    private TextView textoError;
    private MaterialButton botonIngresar;
    private ProgressBar cargando;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.actividad_login);
        campoUsuario = findViewById(R.id.campo_usuario);
        campoClave = findViewById(R.id.campo_clave);
        campoServidor = findViewById(R.id.campo_servidor);
        textoError = findViewById(R.id.texto_error);
        botonIngresar = findViewById(R.id.boton_ingresar);
        cargando = findViewById(R.id.cargando);

        SesionApp.cargarUrl(this);
        campoServidor.setText(SesionApp.SERVICIO.getUrlServidor());
        String aviso = getIntent().getStringExtra(EXTRA_AVISO);
        if (aviso != null) {
            TextView textoAviso = findViewById(R.id.texto_aviso);
            textoAviso.setText(aviso);
            textoAviso.setVisibility(View.VISIBLE);
        }
        botonIngresar.setOnClickListener(v -> ingresar());
    }

    private void ingresar() {
        String usuario = texto(campoUsuario);
        String clave = texto(campoClave);
        if (usuario.isEmpty() || clave.isEmpty()) {
            mostrarError("Ingrese el usuario y la clave.");
            return;
        }
        ocupado(true);
        SesionApp.SERVICIO.setUrlServidor(texto(campoServidor));
        SesionApp.ejecutar(() -> SesionApp.SERVICIO.iniciarSesion(usuario, clave), sesion -> {
            SesionApp.guardarUrl(this);
            startActivity(new Intent(this, ActividadPrincipal.class));
            finish();
        }, error -> {
            ocupado(false);
            campoClave.setText("");
            mostrarError(error.getMessage());
        });
    }

    private void mostrarError(String mensaje) {
        textoError.setText(mensaje);
        textoError.setVisibility(View.VISIBLE);
    }

    private void ocupado(boolean ocupado) {
        botonIngresar.setEnabled(!ocupado);
        botonIngresar.setText(ocupado ? "Verificando..." : "Ingresar");
        cargando.setVisibility(ocupado ? View.VISIBLE : View.GONE);
        if (ocupado) {
            textoError.setVisibility(View.GONE);
        }
    }

    static String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
