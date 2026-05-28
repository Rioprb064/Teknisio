package com.teknisio.dto.responses;

public record RefreshTokenResponse(
  String accessToken,
  String tokenType,
  Long expiresInMs
) {
}
