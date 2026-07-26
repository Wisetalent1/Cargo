package com.nicargo.app.models;

import com.google.gson.annotations.SerializedName;

public class UsernameCheckResponse {
    @SerializedName("available")
    private boolean available;
    
    @SerializedName("message")
    private String message;

    public boolean isAvailable() {
        return available;
    }

    public String getMessage() {
        return message;
    }
}