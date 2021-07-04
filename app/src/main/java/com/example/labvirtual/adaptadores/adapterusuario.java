package com.example.labvirtual.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.labvirtual.R;
import com.example.labvirtual.configuracion.config;
import com.example.labvirtual.modelos.usuarios;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class adapterusuario extends RecyclerView.Adapter<adapterusuario.customViewHolder> implements View.OnClickListener {
    Context contexto;
    List<usuarios> listado;
    private View myView;
    //private String resourceImagenes = "http://192.168.1.78/apiLabVirtual/storage/app/public/";
    private View.OnClickListener listener;


    public adapterusuario(Context contexto, List listado) {
        this.contexto = contexto;
        this.listado = listado;
    }

    /**
     * función sobreescrita customViewHolder, el propósito es mostrar al usuario el RecyclerView
     * Es decir inflar el control
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @NotNull
    @Override
    public customViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater monstrarControl = LayoutInflater.from(parent.getContext());
        myView = monstrarControl.inflate(R.layout.view_users, parent, false);
        myView.setOnClickListener(this); //El objetivo es inicializar el click para capturar en otro momento.
        return new customViewHolder(myView);
    }

    /**
     * Función sobreescrita para sincronizar el json (usuarios) dento cada control en el view_user creado
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull customViewHolder holder, int position) {
        String image = config.getUrlImages();
        holder.rcvId.setText(listado.get(position).getId_usuario());
        holder.rcvNombre.setText(listado.get(position).getNombre());
        holder.rcvApellidos.setText(listado.get(position).getApellido_p() + " " + listado.get(position).getApellido_m());
        holder.rcvRol.setText(listado.get(position).getRoles().getNombre());
        Picasso.with(contexto).load(image + listado.get(position).getImagen()).fit().into(holder.rcvImagen);
    }

    /**
     * Función sobreescrita para procesar la cantidad de filas mostradas al usuario.
     * @return
     */
    @Override
    public int getItemCount() {
        return listado.size();
    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            listener.onClick(v);
        }
    }

    public class customViewHolder extends RecyclerView.ViewHolder{
        //Declarar a nivel de la clase todas las variables a utilizar para enlazarlas mas adelante al layout view_users.
            TextView rcvId, rcvNombre, rcvApellidos, rcvRol;
            ImageView rcvImagen;
            //La variable myView tiene el propósito de manupular el recicler view principal, de esta manera pintar los controles
            //dentro cada vista
            final View myView;
        public customViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            myView = itemView;
            rcvId = myView.findViewById(R.id.txtVUCId);
            rcvNombre = myView.findViewById(R.id.txtVUCNombre);
            rcvApellidos = myView.findViewById(R.id.txtVUCApAm);
            rcvRol = myView.findViewById(R.id.txtVUCRol);
            rcvImagen = myView.findViewById(R.id.imgVUser);
        }
    } //final custom View Holder
    //Implementar la función setOnClickListener
    public void setOnClikListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}

