package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;

public class CancelServiceRequestRequest {
  @SerializedName("cancelReason")
  private String cancelReason;

  public CancelServiceRequestRequest(String cancelReason) {
    this.cancelReason = cancelReason;
  }

  public String getCancelReason() {
    return cancelReason;
  }
}
