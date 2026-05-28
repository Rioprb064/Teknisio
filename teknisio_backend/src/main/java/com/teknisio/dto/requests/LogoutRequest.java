package com.teknisio.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
  @NotBlank(message = "Refresh token wajib diisi")
  String refreshToken
) {
}
