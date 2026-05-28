package com.teknisio.common.response;

import java.util.Map;

public record ApiResponse<T>(
  boolean success,
  String message,
  T data,
  Object errors
) {

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, Map.of());
  }

  public static ApiResponse<Void> success(String message) {
    return new ApiResponse<>(true, message, null, Map.of());
  }

  public static ApiResponse<Void> error(String message) {
    return new ApiResponse<>(false, message, null, Map.of());
  }

  public static ApiResponse<Void> error(String message, Object errors) {
    return new ApiResponse<>(
      false,
      message,
      null,
      errors == null ? Map.of() : errors
    );
  }
}
