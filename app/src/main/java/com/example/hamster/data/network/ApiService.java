package com.example.hamster.data.network;

import com.example.hamster.data.model.LoginResponse;
import com.example.hamster.data.model.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}
