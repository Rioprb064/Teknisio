package com.teknisio.dto.responses;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record TechnicianSummaryResponse(
  UUID technicianProfileId,
  String name,
  String profilePhoto,
  String availabilityStatus,
  BigDecimal averageRating,
  Integer ratingCount,
  Integer totalJobs,
  List<DeviceCategoryResponse> supportedDeviceCategories
) {}
