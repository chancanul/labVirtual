package com.example.labvirtual;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.labvirtual.configuracion.config;
import com.example.labvirtual.modelos.roles;
import com.example.labvirtual.retrofit.interfaceRetrofit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;

public class fragment_detalleusuario extends Fragment {
    private View myView; //Variable para tener accesible la vista en la clase
    private FragmentDetalleusuarioViewModel mViewModel;
    private EditText edTxtNombre, edTxtApellidop, edTxtApellidom, edTxtUsuario, edTxtPassword;
    private TextView txtVId, txtVCStatus;
    private ImageView imgUSer;
    private Spinner spnRoles;
    private String accion = "N";
    private List<roles> listaRoles;
    private String id_rol = "1";
    private ImageButton imgViewBtnCamara;
    private ImageButton imgViewBtnGaleria;
    private Uri imageUri;
    private static final int CAPTURE_IMAGE = 100; //Variable acción de usuario.
    private static final int PICK_IMAGE = 1008;//Variable acción de usuario.
    private Bitmap thumbnail;//Variable para almacenar el mapa de bits.
    private ConstraintLayout backProgress;

    public static fragment_detalleusuario newInstance() {
        return new fragment_detalleusuario();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalleusuario_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity navigation = (MainActivity) getActivity();//Capturar el activity para obtener el fab button.
        FloatingActionButton fabGuardar = navigation.findViewById(R.id.fab);
        fabGuardar.setImageResource(R.drawable.guardar);//Definir la imagen para el fab
        myView = view;
        txtVId = view.findViewById(R.id.duCEtxtid);
        edTxtNombre = view.findViewById(R.id.duCEtxtNombre);
        edTxtApellidop = view.findViewById(R.id.duCEtxtApellidop);
        edTxtApellidom = view.findViewById(R.id.duCEtxtApelldiom);
        edTxtUsuario = view.findViewById(R.id.duCEtxtUsuario);
        edTxtPassword = view.findViewById(R.id.duCEtxtPassword);
        imgUSer = view.findViewById(R.id.duCimgVUsuario);
        spnRoles = view.findViewById(R.id.duCSpnRoles);
        imgViewBtnCamara = view.findViewById(R.id.duCBtnCamara);
        imgViewBtnGaleria = view.findViewById(R.id.duCBtnGaleria);
        backProgress = view.findViewById(R.id.duLytProgress);
        txtVCStatus = view.findViewById(R.id.txtVCProgress);
        //String pathImagen = "http://192.168.1.78/apiLabVirtual/storage/app/public/";
        //Recuperar los datos traidos del fragment anterior.
        getRoles();
        if (getArguments() != null) {
            accion = getArguments().getString("accion"); //Verifica la acción realizada por el usuario
            if (accion == "M") {
                txtVId.setText(getArguments().getString("id_usuario"));
                edTxtNombre.setText(getArguments().getString("nombre"));
                edTxtApellidop.setText(getArguments().getString("apellidop"));
                edTxtApellidom.setText(getArguments().getString("apellidom"));
                edTxtUsuario.setText(getArguments().getString("usuario"));
                edTxtPassword.setText(getArguments().getString("password"));
                String df = config.getUrlImages() + getArguments().getString("imagen");
                Picasso.with(getContext()).load(df).fit().into(imgUSer);
                getRoles();
            } else {
                txtVId.setText("");
                edTxtNombre.setText("");
                edTxtApellidop.setText("");
                edTxtApellidom.setText("");
                edTxtUsuario.setText("");
                edTxtPassword.setText("");
                imgUSer.setImageResource(0);
            }
        }
        /**
         * Función click, va ligada al modelo roles, busca el index dependiento del item seleccionado
         */
        spnRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_rol = listaRoles.get(position).getId_rol();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * Abrir la cámara del dispositivo
         */
        imgViewBtnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enviar datos de identificación al abrir la c+amara por medio del content Values
                ContentValues values;
                values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "My imagen");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo Taken On" + System.currentTimeMillis());
                imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });
        /**
         * Evento clic para abrir la galería
         */
        imgViewBtnGaleria.setOnClickListener(v -> {
            Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);//Intent para abrir la galería.
            startActivityForResult(galeria, PICK_IMAGE);
        });

        fabGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = edTxtNombre.getText().toString().trim();
                String apellidop = edTxtApellidop.getText().toString().trim();
                String apellidom = edTxtApellidom.getText().toString().trim();
                String usuario = edTxtUsuario.getText().toString().trim();
                String password = edTxtPassword.getText().toString().trim();

                imgUSer.setDrawingCacheEnabled(true);
                imgUSer.buildDrawingCache();
                Bitmap bit = ((BitmapDrawable) imgUSer.getDrawable()).getBitmap();
                File file = null;
                try {
                    file = fileConverter(bit);
                } catch (Exception e) {

                }

                txtVCStatus.setText("Mandando datos, Insertando...");
                backProgress.setVisibility(View.VISIBLE);

                RequestBody Req_id_rol = RequestBody.create(MediaType.parse("multipart/form-data"), id_rol);
                RequestBody Req_nombre = RequestBody.create(MediaType.parse("multipart/form-data"), nombre);
                RequestBody Req_apellidop = RequestBody.create(MediaType.parse("multipart/form-data"), apellidop);
                RequestBody Req_apellidom = RequestBody.create(MediaType.parse("multipart/form-data"), apellidom);
                RequestBody Req_usuario = RequestBody.create(MediaType.parse("multipart/form-data"), usuario);
                RequestBody Req_password = RequestBody.create(MediaType.parse("multipart/form-data"), password);

                if (accion == "N") {
                    RequestBody Req_File = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part multiPartBody = MultipartBody.Part.createFormData("imagen", file.getName(), Req_File);
                    addUser( Req_id_rol, Req_nombre, Req_apellidop, Req_apellidom,Req_usuario,Req_password, multiPartBody);
                } else  {
                        //RequestBody Req_File = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        RequestBody Req_File = RequestBody.create(MediaType.parse("image/*"), file);
                        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("imagen", file.getName(), Req_File);
                        MultipartBody.Part methodField = MultipartBody.Part.createFormData("_method", "PUT");
                        updateUser(Req_id_rol, Req_nombre, Req_apellidop, Req_apellidom, Req_usuario, Req_password,methodField, multipartBody );
                }

            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentDetalleusuarioViewModel.class);
        // TODO: Use the ViewModel
    }

    /**
     * Función para traer los roles por medio de retrofit
     */
    private void getRoles() {
        interfaceRetrofit retrofit = config.getRetrofit().create(interfaceRetrofit.class);
        Call<List<roles>> call = retrofit.getRoles("actRoles");
        call.enqueue(new Callback<List<roles>>() {
            @Override
            public void onResponse(Call<List<roles>> call, Response<List<roles>> response) {
                ArrayList<String> data = new ArrayList<String>();
                listaRoles = response.body();
                for (roles listaroles : listaRoles) {
                    data.add(listaroles.getNombre());
                }
                fillSpinnerRol(data);
            }

            @Override
            public void onFailure(Call<List<roles>> call, Throwable t) {
                String s = "";
            }
        });
    }//Fin de getRoles().

    /**
     * Función para llenar el spinner con datos de los roles
     *
     * @param data
     */
    private void fillSpinnerRol(ArrayList<String> data) {
        ArrayAdapter<String> adapterRol = new ArrayAdapter<String>(getContext(), R.layout.bg_spin_text, data);
        adapterRol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRoles.setAdapter(adapterRol);
    }

    /**
     * Función sobrescrita para capturar el activity resultante del usuario pudiendo ser abrir la camara o galeria para
     * elegir una imagen. La respuesta es comparada con variable globales declaradas de tipo entero
     *CAPTURE_IMAGE y PICK_IMAGE asignándole un valor entero el cual debe ser procesado dentro el switch
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //Dentro de la función switch determinar el código de respuesta por parte del usuario final
            case RESULT_OK:
                //Procesar la cámara del disposivo
                if (requestCode == CAPTURE_IMAGE) {
                    int rotate = 0;
                    try {
                        //Girar la imagen de manera vertical, algunas cámaras establecen el moto retrata en paisaje
                        ExifInterface exif = new ExifInterface(getPath(imageUri)); //Traer la ruta de la iamgen capturada
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //La variable thubnail es declarada a nivel de la clase para poder manipularla en distintas funciones
                    //getGuardar imagen, función para comprimir el resultado y girar la imagen, devuelve una matriz de tipo bitMap
                    thumbnail = getGuardarImagen(imageUri, rotate);
                    //Asignar la imagen al ImageView del layout.
                    imgUSer.setImageBitmap(thumbnail);

                } else if (requestCode == PICK_IMAGE) {
                    //Abrir la galería.
                    imageUri = data.getData();
                    //Adignar la imagen elegida al ImageView
                    imgUSer.setImageURI(imageUri);
                }
                break;
        } // fin de swich
    } //Fin onActivityResult

    /**
     * Función para traer la ruta (path) del recurso (Uri)
     * @param uri recurso path del archivo
     * @return ruta del archivo.
     */
    private String getPath(Uri uri) {
        //Arreglo para procesar la ruta por medio de un cursor.
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(colum_index);
        cursor.close();
        return s; // Devuelve la ruta.
    } //Fin de getPath()

    /**
     * Funcióm getGuardarImagen, desompone una imagen en una matriz para luego procesarla en un mapa de bits
     * Devuelve un bitMap con la imagen comprimida.
     * @param recurso
     * @param giro
     * @return
     */
    private Bitmap getGuardarImagen(Uri recurso, int giro) {
        String path = "";
        Bitmap picture = null;
        File imagen;
        try {
            path = getPath(recurso); //Traer la ruta del recurso Uri.
            picture = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), recurso); //Alamacenar en la variable la imagen nueva guardada.
            imagen = new File(path); //Crear
            try {
                FileOutputStream salida = new FileOutputStream(imagen); //Convetir en bytes.
                picture.compress(Bitmap.CompressFormat.JPEG, 50, salida); //Comprimir.
                salida.flush();//Limpiar los recursos de memoria
                salida.close(); //Cerrar el archivo.
                Matrix matrix = new Matrix(); //Nueva imagen de bits.
                matrix.postRotate(giro);//Aplicar la rotación de la imagen ya comprimida.
                //Armar nuevamente la imaen en Bitmap.
                picture = Bitmap.createBitmap(picture,
                        0,
                        0 , picture.getWidth(),
                        picture.getHeight(),
                        matrix,
                        true);
            } catch (Exception e) {

                Log.e("Fallo al intento de gaurdar", e.getMessage());
            } //fin de try

        } catch (Exception e) {

        } //fin de try
        //Retornar la imagen en mapa de Bits.
        return picture;
    } //Fin de getGuardar()

    /**
     * Función para mandar los datos en la petición, incluyendo el archivo de imagen.
     * @param id_rol
     * @param nombre
     * @param apellidop
     * @param apellidom
     * @param usuario
     * @param password
     * @param image
     */
    private void addUser(RequestBody id_rol,
                         RequestBody nombre,
                         RequestBody apellidop,
                         RequestBody apellidom,
                         RequestBody usuario,
                         RequestBody password,
                         MultipartBody.Part image) {
        interfaceRetrofit retrofit = config.getRetrofit().create(interfaceRetrofit.class);
        Call<ResponseBody> call = retrofit.addUser(config.getBaseurl() + "actUser" , id_rol,nombre,apellidop,
                                                   apellidom,usuario,password,image);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                backProgress.setVisibility(View.GONE);
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.nav_home);
                Snackbar mensaje = Snackbar.make(myView, "Los datos fueron guardados con éxito",Snackbar.LENGTH_SHORT);
                mensaje.show();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar mensaje = Snackbar.make(myView, "Servidor inaccesible",Snackbar.LENGTH_SHORT);
                mensaje.show();

            }
        });
    } //Fin addUser

    public void updateUser(RequestBody id_rol,
                           RequestBody nombre,
                           RequestBody apellidop,
                           RequestBody apellidom,
                           RequestBody usuario,
                           RequestBody password,
                           MultipartBody.Part _method,
                           MultipartBody.Part image) {
        interfaceRetrofit retrofit = config.getRetrofit().create(interfaceRetrofit.class);
        Call<ResponseBody> call = retrofit.updateUser(config.getBaseurl() + "actUser/" + getArguments().getString("id_usuario"),
                                                            id_rol, nombre, apellidop, apellidom, usuario, password, _method, image);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Snackbar.make(myView, "El usuario ha sido editado con éxito", Snackbar.LENGTH_SHORT).show();
                NavHostFragment.findNavController(getParentFragment()).navigate(R.id.nav_home);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Snackbar.make(myView, "Fallas en el servidor", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private File fileConverter(Bitmap bitmap) {
        //Crear un nuevo archivo para escribir dentro el mapa de bits.
        File f = new File(getContext().getCacheDir(), "ferchio");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Convertir el bitMap a un arreglo de bytes
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50,bos);
        byte[] bitmapdata = bos.toByteArray();
        //Escribir mapa de bits a array de bytes.
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    } //Fin de fileConverter.
}