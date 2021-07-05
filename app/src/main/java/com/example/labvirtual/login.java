package com.example.labvirtual;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labvirtual.configuracion.config;
import com.example.labvirtual.modelos.usuarios;
import com.example.labvirtual.retrofit.interfaceRetrofit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {
    private EditText usuario, password;
    private int permisoRequerido = 11;
    private TextView pgsTxt;
    private FloatingActionButton fbLogin;
    private ConstraintLayout backProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usuario = findViewById(R.id.edTxtCUsuario);
        password = findViewById(R.id.edTxtPass);
        backProgress = findViewById(R.id.lytProgress);
        pgsTxt = findViewById(R.id.txtVCProgress);
        fbLogin = findViewById(R.id.fabCLogin);
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mostrar el progress layout justo antes de validar
                pgsTxt.setText("Validando Credenciales");
               backProgress.setVisibility(View.VISIBLE);
                //llamar a la función validar
                validar(usuario.getText().toString(), password.getText().toString(), v);
            }
        });
        getPetmisos();
    }

    private void validar(String usuario, String password, View v) {
        //Variable para iniciar la petición Retrofit
        interfaceRetrofit peticion = config.getRetrofit().create(interfaceRetrofit.class);
        //Preparar la petición call (llamar)
        Call<List<usuarios>> call = peticion.validar(usuario, password);
        //Iniciar la petición con enqueue. el método incluye dos apartados para saber si la petición se llevó con éxito o fracaso
        //onResponse-onFailure
        call.enqueue(new Callback<List<usuarios>>() {
            @Override
            public void onResponse(Call<List<usuarios>> call, Response<List<usuarios>> response) {
                //En caso de éxito
                //la variable response es la encargada de almacenar la respuesta del servidor.

                    List <usuarios> users = response.body();
                    //Si la respuesta es correcta se llama al navigation drawer.
                 if(users.get(0) != null)
                    {
                        backProgress.setVisibility(View.GONE);

                        Intent principal = new Intent(getApplicationContext(), MainActivity.class);
                        principal.putExtra("nombre",users.get(0).getNombre());
                        principal.putExtra("apellido_p", users.get(0).getApellido_p());
                        principal.putExtra("imagen", users.get(0).getImagen());
                        startActivity(principal);
                    } else {
                       backProgress.setVisibility(View.GONE);
                        Snackbar msjPersonalizado = Snackbar.make(v, "Usuario o contraseña no válidos", Snackbar.LENGTH_SHORT);
                        msjPersonalizado.show();
                    }
            }

            @Override
            public void onFailure(Call<List<usuarios>> call, Throwable t) {
                //en caso de fracaso
                backProgress.setVisibility(View.GONE); //Hacer invisible el layout progress
                Snackbar msjPersonalizado = Snackbar.make(v, "Servidor inaccesible", Snackbar.LENGTH_SHORT);
                msjPersonalizado.show();
            }
        });
    } //fin validar

    /**
     * Método getPermisos(), verifica los permisos de acceso, escritura y lectura para los recursos de android.
     * almacena la elección del usuario. los permisos requeridos son para la camara, lectura y escritura de la SD.
     */
    private void getPetmisos(){
        int accesLeerSD = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int accespoCamara = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int accesoEscribirSD = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(accesLeerSD != getPackageManager().PERMISSION_GRANTED
                || accespoCamara != getPackageManager().PERMISSION_GRANTED
                || accesoEscribirSD != getPackageManager().PERMISSION_GRANTED) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, permisoRequerido);
            }
        }
    }
}