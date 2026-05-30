package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterCustomerRequest {
    @SerializedName("nama")
    private String nama;

    @SerializedName("email")
    private String email;

    @SerializedName("noTelepon")
    private String noTelepon;

    @SerializedName("password")
    private String password;

    @SerializedName("alamat")
    private String alamat;

    public RegisterCustomerRequest(String nama, String email, String noTelepon, String password, String alamat) {
        this.nama = nama;
        this.email = email;
        this.noTelepon = noTelepon;
        this.password = password;
        this.alamat = alamat;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
}
