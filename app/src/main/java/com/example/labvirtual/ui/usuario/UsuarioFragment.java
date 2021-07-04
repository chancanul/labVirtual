package com.example.labvirtual.ui.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labvirtual.MainActivity;
import com.example.labvirtual.R;
import com.example.labvirtual.adaptadores.adapterusuario;
import com.example.labvirtual.configuracion.config;
import com.example.labvirtual.databinding.FragmentUsuarioBinding;
import com.example.labvirtual.databinding.FragmentUsuarioBinding;
import com.example.labvirtual.modelos.usuarios;
import com.example.labvirtual.retrofit.interfaceRetrofit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioFragment extends Fragment {

    private UsuarioViewModel usuarioViewModel;
    private FragmentUsuarioBinding binding;
    private RecyclerView myRecycler; //Variable para referenciar el recyclerView
    private View myView; //Referenciar la Vista
    private adapterusuario myAdapter; //Referenciar la clase adapterusuario
    private FloatingActionButton fabGlobal;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        usuarioViewModel =
                new ViewModelProvider(this).get(UsuarioViewModel.class);
        binding = FragmentUsuarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        usuarioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    } //fin de onCratedView
    /**
     * Función sobreescrita, esta función no es creada por defecto dentro el fragmento, debe crarse de manera manual
     * Aquí se puede referenciar la vista en una variable para poder mostrar los datos sobre la misma.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     // obtener el MainActivity para poder manipular el floatingActionButton.
        final MainActivity navigation = (MainActivity) getActivity(); //MainActivity Capturado en la variable navigation
        myView = view; //Obtener la vista y tenerla disponible a nivel de la clase
        //Antes de manipular el MainActicity debemos comprobar que no sea nulo.
        if (navigation != null) {
            FloatingActionButton fabGuardar = navigation.findViewById(R.id.fab); //Enlazamos el control fab a una variable
            fabGuardar.setImageResource(R.drawable.guardar_usuario); //estabecemos el fondo o ícono
            fabGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHostFragment.findNavController(getParentFragment()).navigate(R.id.fragment_detalleusuario);
                }
            });
        }
        //Evento clic FloatingActionButton
        traerUsuarios();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    /**
     * Método traerUsuarios, Hace una petición por medio de retrofit en formata json.
     */
    public void traerUsuarios() {
        //Configurar la petición retrofit.
        interfaceRetrofit retrofit = config.getRetrofit().create(interfaceRetrofit.class);
        Call<List<usuarios>> call = retrofit.getUsuarios("actUser");
        call.enqueue(new Callback<List<usuarios>>() {
            @Override
            public void onResponse(Call<List<usuarios>> call, Response<List<usuarios>> response) {
                mostrarRecycler(response.body());
            }

            @Override
            public void onFailure(Call<List<usuarios>> call, Throwable t) {
                Snackbar mensaje = Snackbar.make(myView,"Ha ocurrido un error", Snackbar.LENGTH_SHORT);
                mensaje.show();
            }
        });
    } //fin de traerUsuarios

    /**
     * la función mostrar recicler es la encargada de llamar al control implementado recicler para poder pintarlo en pantalla
     * @param listado
     */
    public void mostrarRecycler(List<usuarios> listado) {
        myRecycler = (RecyclerView) myView.findViewById(R.id.rcvCfragUser); //Definiendo el recycler
        myAdapter = new adapterusuario(getContext(), listado); //Construyendo el adaptador
        myRecycler.setLayoutManager(new LinearLayoutManager(getContext())); //Agregando un contraintLayout al recicler
        myRecycler.setAdapter(myAdapter);//Volcar los datos al recycler
        //Generar la función clic del adaptador, implementado en el adaptador
        myAdapter.setOnClikListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id_usuario", listado.get(myRecycler.getChildLayoutPosition(v)).getId_usuario());
                bundle.putString("id_rol", listado.get(myRecycler.getChildLayoutPosition(v)).getId_rol());
                bundle.putString("nombre", listado.get(myRecycler.getChildLayoutPosition(v)).getNombre());
                bundle.putString("apellidop", listado.get(myRecycler.getChildLayoutPosition(v)).getApellido_p());
                bundle.putString("apellidom", listado.get(myRecycler.getChildLayoutPosition(v)).getApellido_m());
                bundle.putString("usuario", listado.get(myRecycler.getChildLayoutPosition(v)).getUsuario());
                bundle.putString("password", listado.get(myRecycler.getChildLayoutPosition(v)).getPassword());
                bundle.putString("imagen", listado.get(myRecycler.getChildLayoutPosition(v)).getImagen());
                bundle.putString("accion", "M");
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.fragment_detalleusuario, bundle);
            }
        });
    }
}