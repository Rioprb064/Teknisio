package com.teknisio.dto.responses;

import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record RegisterTeknisiResponse(
  UUID userId,
  UUID technicianProfileId,
  String name,
  String email,
  String phoneNumber,
  String address,
  UserRole role,
  UserStatus accountStatus,
  TeknisiStatus availabilityStatus,
  BigDecimal averageRating,
  Integer ratingCount,
  Integer totalJobs,
  String description
) {
}
