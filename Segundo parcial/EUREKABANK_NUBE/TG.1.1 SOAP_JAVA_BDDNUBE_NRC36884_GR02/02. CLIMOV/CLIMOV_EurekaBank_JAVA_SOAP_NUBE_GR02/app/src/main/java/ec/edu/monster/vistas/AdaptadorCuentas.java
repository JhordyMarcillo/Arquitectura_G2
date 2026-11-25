package ec.edu.monster.vistas;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ec.edu.monster.R;
import ec.edu.monster.modelos.DatosCuenta;
import ec.edu.monster.utilidades.Formato;
import java.util.ArrayList;
import java.util.List;

/** Tarjetas de cuenta: codigo, cliente, saldo y estado. */
public class AdaptadorCuentas extends RecyclerView.Adapter<AdaptadorCuentas.Tarjeta> {

    public interface AlTocar {
        void cuenta(String codigo);
    }

    private final List<DatosCuenta> cuentas = new ArrayList<>();
    private final AlTocar alTocar;

    public AdaptadorCuentas(AlTocar alTocar) {
        this.alTocar = alTocar;
    }

    public void cargar(List<DatosCuenta> nuevas) {
        cuentas.clear();
        cuentas.addAll(nuevas);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Tarjeta onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new Tarjeta(LayoutInflater.from(padre.getContext()).inflate(R.layout.item_cuenta, padre, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Tarjeta tarjeta, int posicion) {
        DatosCuenta c = cuentas.get(posicion);
        boolean activa = Formato.esActiva(c);
        tarjeta.codigo.setText(c.getCodigo());
        tarjeta.cliente.setText(c.getCliente());
        tarjeta.saldo.setText(Formato.dinero(c.getMoneda(), c.getSaldo()));
        tarjeta.estado.setText(c.getEstado());
        tarjeta.estado.setTextColor(ContextCompat.getColor(tarjeta.itemView.getContext(), activa ? R.color.verde : R.color.texto_secundario));
        tarjeta.estado.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(tarjeta.itemView.getContext(), activa ? R.color.verde_suave : R.color.fondo)));
        tarjeta.itemView.setOnClickListener(v -> alTocar.cuenta(c.getCodigo()));
    }

    @Override
    public int getItemCount() {
        return cuentas.size();
    }

    static class Tarjeta extends RecyclerView.ViewHolder {
        final TextView codigo;
        final TextView cliente;
        final TextView saldo;
        final TextView estado;

        Tarjeta(View vista) {
            super(vista);
            codigo = vista.findViewById(R.id.texto_codigo);
            cliente = vista.findViewById(R.id.texto_cliente);
            saldo = vista.findViewById(R.id.texto_saldo);
            estado = vista.findViewById(R.id.texto_estado);
        }
    }
}
