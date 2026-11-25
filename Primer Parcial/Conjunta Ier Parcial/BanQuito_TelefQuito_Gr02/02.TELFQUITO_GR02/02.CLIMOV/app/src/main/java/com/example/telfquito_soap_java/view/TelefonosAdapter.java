package com.example.telfquito_soap_java.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telfquito_soap_java.R;
import com.example.telfquito_soap_java.models.TelefonoModel;

import java.io.Serializable;
import java.util.List;

public class TelefonosAdapter extends RecyclerView.Adapter<TelefonosAdapter.TelefonoViewHolder> {

    private List<TelefonoModel> telefonos;
    private Context context;

    public TelefonosAdapter(List<TelefonoModel> telefonos, Context context) {
        this.telefonos = telefonos;
        this.context = context;
    }

    @NonNull
    @Override
    public TelefonoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_telefono, parent, false);
        return new TelefonoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TelefonoViewHolder holder, int position) {
        TelefonoModel telefono = telefonos.get(position);
        holder.tvMarca.setText(telefono.getMarca());
        holder.tvNombre.setText(telefono.getNombre());
        holder.tvPrecio.setText(String.valueOf(telefono.getPrecio()));

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditTelefonoActivity.class);
            intent.putExtra("telefono", (Serializable) telefono);
            context.startActivity(intent);
        });

        holder.btnDesactivar.setText(telefono.getDisponible() == 1 ? "Desactivar" : "Activar");
        holder.btnDesactivar.setOnClickListener(v -> {
            ((TelefonosActivity) context).toggleDisponibilidad(telefono);
            holder.btnDesactivar.setText(telefono.getDisponible() == 1 ? "Desactivar" : "Activar");
            holder.itemView.setBackgroundColor(telefono.getDisponible() == 1 ? Color.WHITE : Color.LTGRAY);
        });

        holder.btnVender.setOnClickListener(v -> {
            Intent intent = new Intent(context, VenderActivity.class);
            intent.putExtra("telefono", (Serializable) telefono);
            context.startActivity(intent);
        });

        holder.itemView.setBackgroundColor(telefono.getDisponible() == 1 ? Color.WHITE : Color.LTGRAY);
    }

    @Override
    public int getItemCount() {
        return telefonos.size();
    }

    public static class TelefonoViewHolder extends RecyclerView.ViewHolder {
        TextView tvMarca, tvNombre, tvPrecio;
        Button btnEditar, btnDesactivar, btnVender;

        public TelefonoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMarca = itemView.findViewById(R.id.tvMarca);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnDesactivar = itemView.findViewById(R.id.btnDesactivar);
            btnVender = itemView.findViewById(R.id.btnVender);
        }
    }
}