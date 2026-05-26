package com.teknisio.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterCustomerRequest(
  @NotBlank(message = "Nama wajib diisi")
  @Size(max = 100, message = "Nama maksimal 100 karakter")
  String nama,

  @NotBlank(message = "Email wajib diisi")
  @Email(message = "Format email tidak valid")
  @Size(max = 100, message = "Email maksimal 100 karakter")
  String email,

  @NotBlank(message = "Nomor telepon wajib diisi")
  @Size(max = 20, message = "Nomor telepon maksimal 20 karakter")
  @Pattern(
    regexp = "^\\+?[0-9]{10,15}$",
    message = "Nomor telepon harus 10-15 digit dan boleh diawali +"
  )
  String noTelepon,

  @NotBlank(message = "Password wajib diisi")
  @Size(min = 8, max = 72, message = "Password harus 8-72 karakter")
  String password,

  @NotBlank(message = "Alamat wajib diisi")
  @Size(max = 500, message = "Alamat maksimal 500 karakter")
  String alamat
){
}
