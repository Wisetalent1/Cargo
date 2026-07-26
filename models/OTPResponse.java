package com.nicargo.app.models;

import com.google.gson.annotations.SerializedName;

public class OTPResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("debug_otp")
    private String debugOTP;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getDebugOTP() {
        return debugOTP;
    }
}