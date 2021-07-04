package com.example.labvirtual.retrofit;

import com.example.labvirtual.modelos.roles;
import com.example.labvirtual.modelos.usuarios;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface interfaceRetrofit {
    //VERBOS HTTP
    /**
     * POST, Validar que el usuario este registrado en la BD.
     * Es de buenas prácticas utilizar POST pues el usaurio y contraseña viajan en la petición.
     * Recordemos que GET expone los datos y POST los oculta
     */
    /**
     * Función en retrofit para mandar los datos y procesar la respuesta
     * @param usuario proporcionar el usuario
     * @param password proporcionar el password
     * @return devuelve el json proporcionado por la API
     */
    @FormUrlEncoded
    @POST("validar") //recordemos que el enrutamiento dentro la API para validar fue definida con el mismo nombre
    Call<ResponseBody> validar(@Field("usuario") String usuario,
                               @Field("password") String password);

    /**
     * GET, listar todos los usuarios de la vd, se conectar al método index de la API
     * @param url
     * @return json con el listado de usuarios.
     */
    @GET
    Call<List<usuarios>>getUsuarios(@Url String url);
    @GET
    Call<List<roles>>getRoles(@Url String url);
    /**
     * Función addUser, para mandar los datos a la API (POST), insertar un nuevo registro
     * @param url
     * @param id_rol
     * @param nombre
     * @param apellido_p
     * @param apellido_m
     * @param usuario
     * @param password
     * @param imagen
     * @return
     */
    @Multipart
    @POST()
    Call<ResponseBody>addUser(@Url String url,
                              @Part("id_rol")RequestBody id_rol,
                              @Part("nombre") RequestBody nombre,
                              @Part("apellido_p") RequestBody apellido_p,
                              @Part("apellido_m") RequestBody apellido_m,
                              @Part("usuario") RequestBody usuario,
                              @Part("password") RequestBody password,
                              @Part MultipartBody.Part imagen);
    @Multipart
    @POST()
    Call<ResponseBody>updateUser(@Url String url,
                                 @Part("id_rol") RequestBody id_rol,
                                 @Part("nombre") RequestBody nombre,
                                 @Part("apellido_p") RequestBody apellido_p,
                                 @Part("apellido_m") RequestBody apellido_m,
                                 @Part("usuario") RequestBody usuario,
                                 @Part("password") RequestBody password,
                                 @Part MultipartBody.Part _method,
                                 @Part MultipartBody.Part imagen);

}
