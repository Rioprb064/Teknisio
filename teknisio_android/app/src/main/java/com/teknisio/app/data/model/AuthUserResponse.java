package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthUserResponse {
    @SerializedName("userId")
    private String userId;

    @SerializedName("technicianProfileId")
    private String technicianProfileId;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("profilePhoto")
    private String profilePhoto;

    @SerializedName("address")
    private String address;

    @SerializedName("role")
    private String role;

    @SerializedName("accountStatus")
    private String accountStatus;

    public String getUserId() {
        return userId;
    }

    public String getTechnicianProfileId() {
        return technicianProfileId;
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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
}
