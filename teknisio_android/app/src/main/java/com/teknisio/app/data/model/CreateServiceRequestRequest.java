package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateServiceRequestRequest {
  @SerializedName("technicianProfileId")
  private String technicianProfileId;

  @SerializedName("deviceCategoryIds")
  private List<String> deviceCategoryIds;

  @SerializedName("issueDescription")
  private String issueDescription;

  @SerializedName("address")
  private String address;

  @SerializedName("addressDetail")
  private String addressDetail;

  public CreateServiceRequestRequest(String technicianProfileId, List<String> deviceCategoryIds, String issueDescription, String address, String addressDetail) {
    this.technicianProfileId = technicianProfileId;
    this.deviceCategoryIds = deviceCategoryIds;
    this.issueDescription = issueDescription;
    this.address = address;
    this.addressDetail = addressDetail;
  }

  public String getTechnicianProfileId() {
    return technicianProfileId;
  }

  public List<String> getDeviceCategoryIds() {
    return deviceCategoryIds;
  }

  public String getIssueDescription() {
    return issueDescription;
  }

  public String getAddress() {
    return address;
  }

  public String getAddressDetail() {
    return addressDetail;
  }
}
