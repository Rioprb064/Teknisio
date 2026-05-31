package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterTeknisiRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    @SerializedName("address")
    private String address;

    @SerializedName("description")
    private String description;

    public RegisterTeknisiRequest(String name, String email, String phoneNumber, String password, String address, String description) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.address = address;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }
}
