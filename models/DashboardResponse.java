package com.nicargo.app.models;

import com.google.gson.annotations.SerializedName;

public class DashboardResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Stats stats;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Stats getStats() {
        return stats;
    }
}
