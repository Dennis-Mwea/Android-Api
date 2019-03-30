package com.example.myapplication.network;

import com.example.myapplication.entities.AccessToken;
import com.example.myapplication.entities.PostResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name, @Field("email") String email, @Field("password") String password);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("email") String username, @Field("password") String password);

    @POST("social_auth")
    @FormUrlEncoded
    Call<AccessToken> socialAuth(@Field("name") String name,
                                 @Field("email") String email,
                                 @Field("provider") String provider,
                                 @Field("provider_user_id") String providerUserId);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("logout")
    @FormUrlEncoded
    Call<AccessToken> logout(@Field("access_token") String accessToken);

    @GET("posts")
    Call<PostResponse> posts();
}