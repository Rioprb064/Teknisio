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

  UUID idUser,
  String nama,
  String email,
  String noTelepon,
  UserRole role,
  UserStatus statusAkun
) {
}
