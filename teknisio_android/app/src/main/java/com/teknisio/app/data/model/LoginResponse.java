package com.teknisio.app.data.model;

public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private String idUser;
    private String nama;
    private String email;
    private String role;

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public String getIdUser() { return idUser; }
    public String getNama() { return nama; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
