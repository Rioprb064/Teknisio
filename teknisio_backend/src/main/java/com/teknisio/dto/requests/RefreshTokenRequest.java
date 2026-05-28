package com.teknisio.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
  @NotBlank(message = "Refresh token wajib diisi")
  String refreshToken
) {
}
