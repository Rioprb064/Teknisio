package com.teknisio.dto.requests;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
  @Size(max = 100, message = "Nama maksimal 100 karakter")
  String nama,

  @Pattern(
    regexp = "^\\+?[0-9]{10,15}$",
    message = "Nomor telepon harus 10-15 digit dan boleh diawali +"
  )
  String noTelepon,

  @Size(max = 500, message = "Alamat maksimal 500 karakter")
  String alamat,

  @Size(max = 1000, message = "URL foto profil maksimal 1000 karakter")
  String fotoProfil
) {
}
