package com.teknisio.dto.responses;

import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TeknisiAuthProfileResponse(
  UUID idUser,
  UUID idTeknisiProfile,
  String nama,
  String email,
  String noTelepon,
  String alamat,
  UserRole role,
  UserStatus statusAkun,
  TeknisiStatus statusKetersediaan,
  BigDecimal ratingAvg,
  Integer ratingCount,
  Integer totalPekerjaan,
  String deskripsi,
  String fotoProfil
)
implements AuthProfileResponse {}
