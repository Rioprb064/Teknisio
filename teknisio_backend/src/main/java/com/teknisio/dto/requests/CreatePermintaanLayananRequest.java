package com.teknisio.dto.requests;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePermintaanLayananRequest(
  @NotNull(message = "Service type ID is required")
  UUID serviceTypeId,

  @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
  @DecimalMax(value = "90.0", message = "Latitude must be at most 90")
  BigDecimal latitude,

  @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
  @DecimalMax(value = "180.0", message = "Longitude must be at most 180")
  BigDecimal longitude,

  @NotBlank(message = "Address is required")
  @Size(max = 1000, message = "Address must be at most 1000 characters")
  String address,

  @Size(max = 1000, message = "Address detail must be at most 1000 characters")
  String addressDetail,

  @NotBlank(message = "Issue description is required")
  @Size(max = 2000, message = "Issue description must be at most 2000 characters")
  String issueDescription
) {
}
