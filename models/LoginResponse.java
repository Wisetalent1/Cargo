package com.nicargo.app.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("token")
    private String token;
    
    @SerializedName("redirect")
    private String redirect;
    
    @SerializedName("user")
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRedirect() {
        return redirect;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        @SerializedName("id")
        private int id;
        
        @SerializedName("name")
        private String name;
        
        @SerializedName("fname")
        private String firstName;
        
        @SerializedName("lname")
        private String lastName;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("username")
        private String username;
        
        @SerializedName("phone")
        private String phone;
        
        @SerializedName("userlevel")
        private int userLevel;

        public int getId() {
            return id;
        }

        public String getName() {
            return name != null ? name : (firstName + " " + lastName);
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getPhone() {
            return phone;
        }

        public int getUserLevel() {
            return userLevel;
        }
    }
}