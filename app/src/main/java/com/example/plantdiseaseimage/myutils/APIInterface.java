package com.example.plantdiseaseimage.myutils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface APIInterface {
    @GET("everything")
    Call<ResponseModel> getLatestNews(@Query("sources") String source,@Query("q") String q,@Query("apiKey") String apiKey);
}
