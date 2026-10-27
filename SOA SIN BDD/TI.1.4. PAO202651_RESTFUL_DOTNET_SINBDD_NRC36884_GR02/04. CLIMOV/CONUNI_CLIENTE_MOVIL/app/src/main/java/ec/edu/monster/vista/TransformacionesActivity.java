package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ApiService;
import ec.edu.monster.modelo.ConversionRequest;
import ec.edu.monster.modelo.ConversionResponse;

public class TransformacionesActivity extends AppCompatActivity {

    private TextView tvTitulo, tvResultado;
    private Spinner spinnerOrigen, spinnerDestino;
    private EditText etValor;
    private Button btnConvertir, btnVolver, btnLimpiar;
    private ImageButton btnAtras;
    private ProgressBar progressBar;
    private ImageView ivIconoResultado, ivIconoCategoria;

    private String tipoConversion;   // "Temperatura" (texto en pantalla)
    private String categoria;        // "TEMPERATURA" (codigo del servidor)
    private final List<String> unidades = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transformaciones);

        // Obtener el tipo de conversión
        tipoConversion = getIntent().getStringExtra("TIPO_CONVERSION");
        categoria = tipoConversion.toUpperCase(Locale.ROOT);

        // Inicializar vistas
        tvTitulo = findViewById(R.id.tvTitulo);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        etValor = findViewById(R.id.etValor);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnVolver = findViewById(R.id.btnVolver);
        btnAtras = findViewById(R.id.btnAtras);
        tvResultado = findViewById(R.id.tvResultado);
        progressBar = findViewById(R.id.progressBar);
        ivIconoResultado = findViewById(R.id.ivIconoResultado);
        ivIconoCategoria = findViewById(R.id.ivIconoCategoria);

        // Inicializar servicio API
        apiService = new ApiService();

        // Configurar título
        tvTitulo.setText("Conversión de " + tipoConversion);

        // Configurar iconos
        actualizarIcono();
        actualizarIconoCategoria();

        // Listeners
        btnConvertir.setOnClickListener(v -> realizarConversion());
        btnLimpiar.setOnClickListener(v -> limpiarCampos());
        btnVolver.setOnClickListener(v -> finish());
        btnAtras.setOnClickListener(v -> finish());

        // Unidades desde el servidor
        cargarUnidades();
    }

    private void limpiarCampos() {
        etValor.setText("");
        tvResultado.setText("Ingrese los datos y presione CONVERTIR");
        etValor.requestFocus();
    }

    private int obtenerIcono() {
        switch (tipoConversion) {
            case "Temperatura":
                return R.drawable.ic_temperature;
            case "Masa":
                return R.drawable.ic_weight;
            case "Longitud":
                return R.drawable.ic_ruler;
            default:
                return R.drawable.ic_result;
        }
    }

    private void actualizarIcono() {
        if (ivIconoResultado != null) {
            ivIconoResultado.setImageResource(obtenerIcono());
        }
    }

    private void actualizarIconoCategoria() {
        if (ivIconoCategoria != null) {
            ivIconoCategoria.setImageResource(obtenerIcono());
        }
    }

    // El servidor envia los codigos sin tilde; solo para mostrarlos
    private static final Map<String, String> NOMBRES_CON_TILDE = new HashMap<>();
    static {
        NOMBRES_CON_TILDE.put("MILIMETRO", "Milímetro");
        NOMBRES_CON_TILDE.put("CENTIMETRO", "Centímetro");
        NOMBRES_CON_TILDE.put("KILOMETRO", "Kilómetro");
    }

    // "KILOMETRO" -> "Kilómetro", "METRO" -> "Metro"
    private String nombreVisible(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return "";
        }
        String conTilde = NOMBRES_CON_TILDE.get(codigo);
        if (conTilde != null) {
            return conTilde;
        }
        return codigo.substring(0, 1) + codigo.substring(1).toLowerCase(Locale.ROOT);
    }

    // CU-03 Consultar unidades por categoria
    private void cargarUnidades() {
        progressBar.setVisibility(View.VISIBLE);
        btnConvertir.setEnabled(false);

        apiService.obtenerUnidades(categoria, new ApiService.ApiCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> respuesta) {
                progressBar.setVisibility(View.GONE);
                btnConvertir.setEnabled(true);

                unidades.clear();
                unidades.addAll(respuesta);

                List<String> nombres = new ArrayList<>();
                for (String unidad : unidades) {
                    nombres.add(nombreVisible(unidad));
                }
                spinnerOrigen.setAdapter(crearAdapter(nombres));
                spinnerDestino.setAdapter(crearAdapter(nombres));
                if (unidades.size() > 1) {
                    spinnerDestino.setSelection(1);
                }
            }

            @Override
            public void onError(String codigo, String mensaje) {
                progressBar.setVisibility(View.GONE);
                manejarError(codigo, mensaje);
            }
        });
    }

    private ArrayAdapter<String> crearAdapter(List<String> nombres) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    // CU-04 Realizar conversion
    private void realizarConversion() {
        if (unidades.isEmpty()) {
            Toast.makeText(this, "Las unidades aún no se han cargado", Toast.LENGTH_SHORT).show();
            return;
        }

        String valorStr = etValor.getText().toString().trim().replace(',', '.');

        if (valorStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un valor", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        String unidadOrigen = unidades.get(spinnerOrigen.getSelectedItemPosition());
        String unidadDestino = unidades.get(spinnerDestino.getSelectedItemPosition());

        // Crear request
        ConversionRequest request = new ConversionRequest(categoria, unidadOrigen, unidadDestino, valor);

        // Mostrar loading
        progressBar.setVisibility(View.VISIBLE);
        btnConvertir.setEnabled(false);
        tvResultado.setText("");

        // Llamar a la API
        apiService.realizarConversion(request, new ApiService.ApiCallback<ConversionResponse>() {
            @Override
            public void onSuccess(ConversionResponse response) {
                progressBar.setVisibility(View.GONE);
                btnConvertir.setEnabled(true);

                String resultado = String.format(Locale.getDefault(), "%s %s =\n%.4f %s",
                        valorStr, nombreVisible(response.getUnidadOrigen()),
                        response.getResultado(), nombreVisible(response.getUnidadDestino()));
                tvResultado.setText(resultado);
                tvResultado.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String codigo, String mensaje) {
                progressBar.setVisibility(View.GONE);
                btnConvertir.setEnabled(true);
                manejarError(codigo, mensaje);
            }
        });
    }

    // CU-08 a CU-11
    private void manejarError(String codigo, String mensaje) {
        if (isFinishing()) {
            return;
        }
        if (LoginActivity.esErrorDeSesion(codigo)) {
            LoginActivity.volverAlLogin(this, mensaje);
            return;
        }
        tvResultado.setText("⚠️ " + mensaje);
        if (ApiService.ERROR_RED.equals(codigo)) {
            LoginActivity.mostrarErrorConexion(this, mensaje);
        } else {
            Toast.makeText(this, "Error: [" + codigo + "] " + mensaje, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiService != null) {
            apiService.shutdown();
        }
    }
}
