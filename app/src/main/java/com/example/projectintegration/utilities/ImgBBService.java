package com.example.projectintegration.utilities;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Field;

// Define la interfaz para la API de ImgBB
public interface ImgBBService {
    @FormUrlEncoded
    @POST("upload")
    Call<UploadResponse> uploadImage(
            @Field("key") String apiKey,       // Tu clave de API
            @Field("image") String imageBase64  // La imagen en formato base64
    );
}
