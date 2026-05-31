package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ServiceRequestResponse {
  @SerializedName("serviceRequestId")
  private String serviceRequestId;

  @SerializedName("serviceRequestCode")
  private String serviceRequestCode;

  @SerializedName("customerId")
  private String customerId;

  @SerializedName("technicianProfileId")
  private String technicianProfileId;

  @SerializedName("status")
  private String status;

  @SerializedName("issueDescription")
  private String issueDescription;

  @SerializedName("address")
  private String address;

  @SerializedName("addressDetail")
  private String addressDetail;

  @SerializedName("cancelReason")
  private String cancelReason;

  @SerializedName("selectedDeviceCategories")
  private List<DeviceCategoryResponse> selectedDeviceCategories;

  @SerializedName("requestTime")
  private String requestTime;

  @SerializedName("cancelledAt")
  private String cancelledAt;

  public String getServiceRequestId() {
    return serviceRequestId;
  }

  public String getServiceRequestCode() {
    return serviceRequestCode;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getTechnicianProfileId() {
    return technicianProfileId;
  }

  public String getStatus() {
    return status;
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

  public String getCancelReason() {
    return cancelReason;
  }

  public List<DeviceCategoryResponse> getSelectedDeviceCategories() {
    return selectedDeviceCategories;
  }

  public String getRequestTime() {
    return requestTime;
  }

  public String getCancelledAt() {
    return cancelledAt;
  }
}
