package com.teknisio.services;

import com.teknisio.dto.requests.RegisterCustomerRequest;
import com.teknisio.dto.requests.RegisterTeknisiRequest;
import com.teknisio.dto.responses.RegisterCustomerResponse;
import com.teknisio.dto.responses.RegisterTeknisiResponse;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.TeknisiProfileRepository;
import com.teknisio.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final TeknisiProfileRepository teknisiProfileRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request) {
    String nama = request.nama().trim();
    String email = normalizeEmail(request.email());
    String noTelepon = request.noTelepon().trim();
    String alamat = request.alamat().trim();

    validateEmailAndPhoneUnique(email, noTelepon);

    User user = User.builder()
      .nama(nama)
      .email(email)
      .noTelepon(noTelepon)
      .passwordHash(passwordEncoder.encode(request.password()))
      .alamat(alamat)
      .role(UserRole.CUSTOMER)
      .statusAkun(UserStatus.ACTIVE)
      .build()
    ;

    User savedUser = userRepository.save(user);

    return new RegisterCustomerResponse(
      savedUser.getIdUser(),
      savedUser.getNama(),
      savedUser.getEmail(),
      savedUser.getNoTelepon(),
      savedUser.getAlamat(),
      savedUser.getRole(),
      savedUser.getStatusAkun()
    );
  }

  @Transactional
  public RegisterTeknisiResponse registerTeknisi(RegisterTeknisiRequest request) {
    String nama = request.nama().trim();
    String email = normalizeEmail(request.email());
    String noTelepon = request.noTelepon().trim();
    String alamat = request.alamat().trim();
    String deskripsi = request.deskripsi().trim();

    validateEmailAndPhoneUnique(email, noTelepon);

    User user = User.builder()
      .nama(nama)
      .email(email)
      .noTelepon(noTelepon)
      .passwordHash(passwordEncoder.encode(request.password()))
      .alamat(alamat)
      .role(UserRole.TEKNISI)
      .statusAkun(UserStatus.ACTIVE)
      .build()
    ;

    User savedUser = userRepository.save(user);

    TeknisiProfile teknisiProfile = TeknisiProfile.builder()
      .user(savedUser)
      .statusKetersediaan(TeknisiStatus.OFFLINE)
      .ratingAvg(BigDecimal.ZERO)
      .ratingCount(0)
      .totalPekerjaan(0)
      .deskripsi(deskripsi)
      .build()
    ;

    TeknisiProfile savedProfile = teknisiProfileRepository.save(teknisiProfile);

    return new RegisterTeknisiResponse(
      savedUser.getIdUser(),
      savedProfile.getIdTeknisiProfile(),
      savedUser.getNama(),
      savedUser.getEmail(),
      savedUser.getNoTelepon(),
      savedUser.getAlamat(),
      savedUser.getRole(),
      savedUser.getStatusAkun(),
      savedProfile.getStatusKetersediaan(),
      savedProfile.getRatingAvg(),
      savedProfile.getRatingCount(),
      savedProfile.getTotalPekerjaan(),
      savedProfile.getDeskripsi()
    );
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase();
  }

  private void validateEmailAndPhoneUnique(String email, String noTelepon) {
    if (userRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah digunakan");
    }

    if (userRepository.existsByNoTeleponAndDeletedAtIsNull(noTelepon)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nomor telepon sudah digunakan");
    }
  }
}
