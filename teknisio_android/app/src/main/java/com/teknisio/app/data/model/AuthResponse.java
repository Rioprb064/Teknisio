package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("tokenType")
    private String tokenType;

    @SerializedName("expiresInMs")
    private Long expiresInMs;

    @SerializedName("user")
    private AuthUserResponse user;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresInMs() {
        return expiresInMs;
    }

    public AuthUserResponse getUser() {
        return user;
    }
}
