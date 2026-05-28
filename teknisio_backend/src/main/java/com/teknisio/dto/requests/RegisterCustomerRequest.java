package com.teknisio.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name must be at most 100 characters")
  String name,

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 100, message = "Email must be at most 100 characters")
  String email,

  @NotBlank(message = "Phone number is required")
  @Size(max = 20, message = "Phone number must be at most 20 characters")
  @Pattern(
    regexp = "^\\+?[0-9]{10,15}$",
    message = "Phone number must be 10-15 digits and may start with +"
  )
  String phoneNumber,

  @NotBlank(message = "Password is required")
  @Size(min = 8, max = 72, message = "Password must be 8-72 characters")
  String password,

  @NotBlank(message = "Address is required")
  @Size(max = 500, message = "Address must be at most 500 characters")
  String address
) {
}
