package com.example.sparepart2.ChatHandling;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/")
    Call<ResponseBody> sendMessage(@Body RequestBody requestBody);
}
