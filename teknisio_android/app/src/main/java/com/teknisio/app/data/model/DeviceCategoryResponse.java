package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;

public class DeviceCategoryResponse {
  @SerializedName("deviceCategoryId")
  private String deviceCategoryId;

  @SerializedName("name")
  private String name;

  @SerializedName("icon")
  private String icon;

  public String getDeviceCategoryId() {
    return deviceCategoryId;
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }
}
