package com.example.weather.finedust.data.service;

import com.example.weather.finedust.data.model.tmcoordinates.TmCoordinatesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface KakaoLocalApiService {

    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    @GET("v2/local/geo/transcoord.json?output_coord=TM")
    Call<TmCoordinatesResponse> getTmCoordinates(
        @Query("x") double longitude,
        @Query("y") double latitude
    );
}
