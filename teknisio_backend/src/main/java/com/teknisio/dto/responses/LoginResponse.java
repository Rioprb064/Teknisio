package com.teknisio.dto.responses;

import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.util.UUID;

public record LoginResponse(
  String accessToken,
  String refreshToken,
  String tokenType,
  Long expiresInMs,
  Long refreshExpiresInMs,

  UUID userId,
  String name,
  String email,
  String phoneNumber,
  UserRole role,
  UserStatus accountStatus
) {
}
