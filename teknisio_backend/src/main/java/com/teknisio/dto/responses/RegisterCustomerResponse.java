package com.teknisio.dto.responses;

import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.util.UUID;

public record RegisterCustomerResponse(
  UUID idUser,
  String nama,
  String email,
  String noTelepon,
  String alamat,
  UserRole role,
  UserStatus statusAkun
) {
}
