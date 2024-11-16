package com.example.projectintegration.utilities;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CloudinaryService {

    // Endpoint para subir la imagen a Cloudinary
    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadImage(
            @Part MultipartBody.Part file,
            @Query("upload_preset") String uploadPreset,
            @Query("api_key") String apiKey,
            @Query("timestamp") String timestamp,
            @Query("signature") String signature
    );
}
