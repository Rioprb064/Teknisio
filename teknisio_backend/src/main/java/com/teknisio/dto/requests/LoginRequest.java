package com.teknisio.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
  @NotBlank(message = "Email Wajib Diisi")
  @Email(message = "Format Tidak Valid")
  @Size(max = 100, message = "Email Maksimal 100 Karakter")
  String email,

  @NotBlank(message = "Password Wajib Diisi")
  @Size(min = 8, message = "Password Minimal 8 Karakter")
  String password
) {
}
