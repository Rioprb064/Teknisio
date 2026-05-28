package com.teknisio.dto.responses;

import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.util.UUID;

public record RegisterCustomerResponse(
  UUID userId,
  String name,
  String email,
  String phoneNumber,
  String address,
  UserRole role,
  UserStatus accountStatus
) {
}
