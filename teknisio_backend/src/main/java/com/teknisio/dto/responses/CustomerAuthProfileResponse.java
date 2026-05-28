package com.teknisio.dto.responses;

import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.util.UUID;

public record CustomerAuthProfileResponse(
  UUID userId,
  String name,
  String email,
  String phoneNumber,
  String address,
  UserRole role,
  UserStatus accountStatus,
  String profilePhoto
)
implements AuthProfileResponse {}
