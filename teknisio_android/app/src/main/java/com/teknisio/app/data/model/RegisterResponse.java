package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("idUser")
    private String idUser;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("nama")
    private String nama;

    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
}
