package com.teknisio.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record UpdateTechnicianAvailabilityStatusRequest(
  @NotBlank(message = "Availability status is required")
  String availabilityStatus
) {}
