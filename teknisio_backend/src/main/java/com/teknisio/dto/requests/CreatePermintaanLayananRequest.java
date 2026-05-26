package com.teknisio.dto.requests;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePermintaanLayananRequest(
  @NotNull(message = "ID layanan wajib diisi")
  UUID idLayanan,

  @DecimalMin(value = "-90.0", message = "Latitude minimal -90")
  @DecimalMax(value = "90.0", message = "Latitude maksimal 90")
  BigDecimal latitude,

  @DecimalMin(value = "-180.0", message = "Longitude minimal -180")
  @DecimalMax(value = "180.0", message = "Longitude maksimal 180")
  BigDecimal longitude,

  @NotBlank(message = "Alamat wajib diisi")
  @Size(max = 1000, message = "Alamat maksimal 1000 karakter")
  String alamat,

  @Size(max = 1000, message = "Detail alamat maksimal 1000 karakter")
  String detailAlamat,

  @NotBlank(message = "Deskripsi masalah wajib diisi")
  @Size(max = 2000, message = "Deskripsi masalah maksimal 2000 karakter")
  String deskripsiMasalah) {
}
