package com.teknisio.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, String message, T data, Object errors) {

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data, null);
  }

  public static ApiResponse<Map<String, Object>> success(String message) {
    return new ApiResponse<>(true, message, Map.of(), null);
  }

  public static ApiResponse<Void> error(String message) {
    return new ApiResponse<>(false, message, null, Map.of());
  }

  public static ApiResponse<Void> error(String message, Object errors) {
    return new ApiResponse<>(false, message, null, errors);
  }
}
