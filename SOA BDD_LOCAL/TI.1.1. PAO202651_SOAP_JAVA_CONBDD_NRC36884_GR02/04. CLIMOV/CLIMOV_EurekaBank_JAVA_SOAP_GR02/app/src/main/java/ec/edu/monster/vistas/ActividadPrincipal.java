package ec.edu.monster.vistas;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import ec.edu.monster.R;
import ec.edu.monster.controladores.SesionApp;
import ec.edu.monster.modelos.ExcepcionServicio;

/** Pestanas principales: Cuentas, Movimientos y Operar. */
public class ActividadPrincipal extends AppCompatActivity {

    private BottomNavigationView navegacion;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        if (SesionApp.SERVICIO.getSesion() == null) {
            volverAlLogin(null);
            return;
        }
        setContentView(R.layout.actividad_principal);
        navegacion = findViewById(R.id.navegacion);
        navegacion.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            mostrar(id == R.id.menu_movimientos ? FragmentoMovimientos.nuevo(null)
                    : id == R.id.menu_operar ? new FragmentoOperaciones() : new FragmentoCuentas());
            return true;
        });
        if (estado == null) {
            mostrar(new FragmentoCuentas());
        }
    }

    private void mostrar(Fragment fragmento) {
        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor, fragmento).commit();
    }

    /** Abre la pestana de movimientos con la cuenta indicada. */
    public void verMovimientos(String cuenta) {
        navegacion.getMenu().findItem(R.id.menu_movimientos).setChecked(true);
        mostrar(FragmentoMovimientos.nuevo(cuenta));
    }

    /** Si la sesion expiro vuelve al login; devuelve true en ese caso. */
    public boolean manejarSesion(ExcepcionServicio error) {
        if (error.requiereLogin()) {
            volverAlLogin(error.getMessage() + ". Inicie sesion nuevamente.");
            return true;
        }
        return false;
    }

    public void volverAlLogin(String aviso) {
        SesionApp.SERVICIO.cerrarSesion();
        Intent intento = new Intent(this, ActividadLogin.class);
        if (aviso != null) {
            intento.putExtra(ActividadLogin.EXTRA_AVISO, aviso);
        }
        startActivity(intento);
        finish();
    }
}
