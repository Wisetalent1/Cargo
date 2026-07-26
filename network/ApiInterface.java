package com.nicargo.app.network;

import com.nicargo.app.models.DashboardResponse;
import com.nicargo.app.models.LoginResponse;
import com.nicargo.app.models.RegistrationResponse;
import com.nicargo.app.models.OTPResponse;
import com.nicargo.app.models.UsernameCheckResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/auth/login")
    Call<LoginResponse> login(
        @Field("login") String login,
        @Field("password") String password
    );

    @GET("/dashboard/stats")
    Call<DashboardResponse> getDashboardStats(
        @Header("Authorization") String authToken
    );
    
    @FormUrlEncoded
    @POST("/auth/register")
    Call<RegistrationResponse> register(
        @Field("email") String email,
        @Field("firstName") String firstName,
        @Field("lastName") String lastName,
        @Field("username") String username,
        @Field("phone") String phone,
        @Field("state") String state,
        @Field("password") String password,
        @Field("countryCode") String countryCode,
        @Field("countryName") String countryName
    );
    
    @FormUrlEncoded
    @POST("/auth/send-otp")
    Call<OTPResponse> sendOTP(
        @Field("email") String email
    );
    
    @FormUrlEncoded
    @POST("/auth/verify-otp")
    Call<OTPResponse> verifyOTP(
        @Field("email") String email,
        @Field("otp") String otp
    );
    
    @FormUrlEncoded
    @POST("/auth/check-username")
    Call<UsernameCheckResponse> checkUsername(
        @Field("username") String username
    );
    
    @FormUrlEncoded
    @POST("/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(
        @Field("email") String email
    );
}