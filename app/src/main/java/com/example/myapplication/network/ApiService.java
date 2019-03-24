package com.example.myapplication.network;

import com.example.myapplication.entities.AccessToken;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    @FormUrlEncoded
    Call<AccessToken>
}
