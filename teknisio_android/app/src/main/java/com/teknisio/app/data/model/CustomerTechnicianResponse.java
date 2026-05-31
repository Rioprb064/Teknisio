package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CustomerTechnicianResponse {
  @SerializedName("technicianProfileId")
  private String technicianProfileId;

  @SerializedName("name")
  private String name;

  @SerializedName("profilePhoto")
  private String profilePhoto;

  @SerializedName("availabilityStatus")
  private String availabilityStatus;

  @SerializedName("averageRating")
  private Double averageRating;

  @SerializedName("ratingCount")
  private Integer ratingCount;

  @SerializedName("totalJobs")
  private Integer totalJobs;

  @SerializedName("description")
  private String description;

  @SerializedName("supportedDeviceCategories")
  private List<DeviceCategoryResponse> supportedDeviceCategories;

  public String getTechnicianProfileId() {
    return technicianProfileId;
  }

  public String getName() {
    return name;
  }

  public String getProfilePhoto() {
    return profilePhoto;
  }

  public String getAvailabilityStatus() {
    return availabilityStatus;
  }

  public Double getAverageRating() {
    return averageRating;
  }

  public Integer getRatingCount() {
    return ratingCount;
  }

  public Integer getTotalJobs() {
    return totalJobs;
  }

  public String getDescription() {
    return description;
  }

  public List<DeviceCategoryResponse> getSupportedDeviceCategories() {
    return supportedDeviceCategories;
  }
}
