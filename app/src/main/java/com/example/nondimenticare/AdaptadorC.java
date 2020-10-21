package com.example.nondimenticare;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorC extends RecyclerView.Adapter<AdaptadorC.ViewHolderDatos> {
    private Context context;
    ArrayList<String> lista_c;

    public class ViewHolderDatos extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNombre, tvFecha, tvFaltan;
        ImageButton ibtnEditar, ibtnEliminar;
        ImageView ibtnCake;
        Intent intent;

        public ViewHolderDatos(@NonNull final View itemView) {
            super(itemView);
            context = itemView.getContext();

            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
            tvFecha = (TextView) itemView.findViewById(R.id.tvFecha);
            tvFaltan = (TextView) itemView.findViewById(R.id.tvFaltan);
            ibtnEditar = (ImageButton) itemView.findViewById(R.id.ibtnEditar);
            ibtnEliminar = (ImageButton) itemView.findViewById(R.id.ibtnEliminar);
            ibtnCake = (ImageView) itemView.findViewById(R.id.ibtnCake);
        }
        
        public void setOnClickListener() {
            ibtnEditar.setOnClickListener(this);
            ibtnEliminar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ibtnEditar:
                    intent = new Intent(context, Actualiza.class);
                    intent.putExtra("nombre", tvNombre.getText());
                    intent.putExtra("cumple", tvFecha.getText());
                    context.startActivity(intent);
                    break;
                case R.id.ibtnEliminar:
                    intent = new Intent(context, Eliminar.class);
                    intent.putExtra("nombre", tvNombre.getText());
                    intent.putExtra("cumple", tvFecha.getText());
                    intent.putExtra("faltan", tvFaltan.getText());
                    context.startActivity(intent);
                    break;
            }
        }
    }

    public AdaptadorC(ArrayList<String> lista_c) {
        this.lista_c = lista_c;
    }

    @NonNull
    @Override
    public AdaptadorC.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_c, parent, false);
        context = parent.getContext();
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorC.ViewHolderDatos holder, int position) {
        String s = lista_c.get(position);
        holder.tvNombre.setText(s.split(";")[0]);

        int mes = Integer.parseInt(s.split(";")[1]);
        int dia = Integer.parseInt(s.split(";")[2]);
        int dfaltan = Integer.parseInt(s.split(";")[3]);

        CalculosFechas cf = new CalculosFechas();
        String cumple = dia + " " + cf.nombreMes(mes);
        holder.tvFecha.setText("Cumpleaños: " +cumple);

        if(dfaltan == 0){
            holder.tvFaltan.setText("¡Hoy es su cumpleaños!");
            holder.ibtnCake.setVisibility(View.VISIBLE);
            holder.tvFaltan.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary2));
        }else {
            if (dfaltan == 1) {
                holder.tvFaltan.setText("Falta: " + dfaltan + " día.");
            }else {
                holder.tvFaltan.setText("Faltan: " + dfaltan + " días.");
            }
            holder.ibtnCake.setVisibility(View.GONE);
            holder.tvFaltan.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
        }
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return lista_c.size();
    }

}