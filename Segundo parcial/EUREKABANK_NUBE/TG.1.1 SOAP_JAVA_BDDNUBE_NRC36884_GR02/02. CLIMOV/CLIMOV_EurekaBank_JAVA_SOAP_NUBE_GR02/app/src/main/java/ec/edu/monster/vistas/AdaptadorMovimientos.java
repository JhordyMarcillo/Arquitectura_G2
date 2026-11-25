package ec.edu.monster.vistas;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ec.edu.monster.R;
import ec.edu.monster.modelos.DatosMovimiento;
import ec.edu.monster.utilidades.Formato;
import java.util.ArrayList;
import java.util.List;

/** Tarjetas del historial: flecha de color, descripcion, fecha/referencia e importe con signo. */
public class AdaptadorMovimientos extends RecyclerView.Adapter<AdaptadorMovimientos.Tarjeta> {

    private final List<DatosMovimiento> movimientos = new ArrayList<>();
    private String moneda = "Soles";

    public void cargar(List<DatosMovimiento> nuevos, String monedaCuenta) {
        movimientos.clear();
        movimientos.addAll(nuevos);
        moneda = monedaCuenta;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Tarjeta onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new Tarjeta(LayoutInflater.from(padre.getContext()).inflate(R.layout.item_movimiento, padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Tarjeta tarjeta, int posicion) {
        DatosMovimiento m = movimientos.get(posicion);
        boolean ingreso = Formato.esIngreso(m);
        int color = ContextCompat.getColor(tarjeta.itemView.getContext(), ingreso ? R.color.verde : R.color.rojo);
        int fondo = ContextCompat.getColor(tarjeta.itemView.getContext(), ingreso ? R.color.verde_suave : R.color.rojo_suave);
        tarjeta.icono.setImageResource(ingreso ? R.drawable.ic_arriba : R.drawable.ic_abajo);
        tarjeta.icono.setBackgroundTintList(ColorStateList.valueOf(fondo));
        tarjeta.descripcion.setText(m.getDescripcion());
        String detalle = "N° " + m.getNumero() + "  ·  " + m.getFecha();
        tarjeta.detalle.setText(m.getCuentaReferencia() == null ? detalle : detalle + "  ·  " + m.getCuentaReferencia());
        tarjeta.importe.setText((ingreso ? "+ " : "- ") + Formato.dinero(moneda, m.getImporte()));
        tarjeta.importe.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return movimientos.size();
    }

    static class Tarjeta extends RecyclerView.ViewHolder {
        final ImageView icono;
        final TextView descripcion;
        final TextView detalle;
        final TextView importe;

        Tarjeta(View vista) {
            super(vista);
            icono = vista.findViewById(R.id.icono);
            descripcion = vista.findViewById(R.id.texto_descripcion);
            detalle = vista.findViewById(R.id.texto_detalle);
            importe = vista.findViewById(R.id.texto_importe);
        }
    }
}
