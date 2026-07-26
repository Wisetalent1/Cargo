package com.nicargo.app.models;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}