package com.teknisio.dto.responses;

import java.util.UUID;

public record TechnicianAvailabilityStatusResponse(
  UUID technicianProfileId,
  String availabilityStatus
) {}
