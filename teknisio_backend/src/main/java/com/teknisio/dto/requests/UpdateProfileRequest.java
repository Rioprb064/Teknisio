package com.teknisio.dto.requests;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
  @Size(max = 100, message = "Name must be at most 100 characters")
  String name,

  @Pattern(
    regexp = "^\\+?[0-9]{10,15}$",
    message = "Phone number must be 10-15 digits and may start with +"
  )
  String phoneNumber,

  @Size(max = 500, message = "Address must be at most 500 characters")
  String address,

  @Size(max = 1000, message = "Profile photo URL must be at most 1000 characters")
  String profilePhoto
) {
}
