package com.example.labvirtual.configuracion;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class config {
    //Variable _BASEURL, se encarga de almacenar la dirección ip del servidor o nombre de dominio cuando el host
    //se encuentre en un servidor remoto
    private static final String _IP_SERVER = "http://192.168.1.74/";
    private static final String _NOMBRE_PROYECTO = "apiLabVirtual/";
    private static final String _URI = "public/api/";
    private static final String _URI_IMAGES = "storage/app/public/";
    private static final String _BASEURL = _IP_SERVER + _NOMBRE_PROYECTO + _URI;
    //    //Una variable con el tipo de dato retrofit para empezar a procesar la petición.
    private static Retrofit retrofit;
    //función para crear la petición
    private static final String _URL_IMAGES = _IP_SERVER + _NOMBRE_PROYECTO + _URI_IMAGES;

    /**
     * Función retrofit, es encargada de armar la petición http, prepara los valores del usuario mediante los
     * verbos http get, post. crear en objetos json los datos del usuario para mandar y decodifica el resultado.
     * @return json de tipo retrofit.
     */
    public static Retrofit getRetrofit() {
        // Debemos verificar que la variable retrofit sea nula, para poder armar la petición.
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(_BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static String getUrlImages() {
        return _URL_IMAGES;
    }

    public static String getBaseurl() {
        return _BASEURL;
    }
}
