package com.teknisio.services;

import com.teknisio.dto.requests.RegisterCustomerRequest;
import com.teknisio.dto.responses.RegisterCustomerResponse;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request) {
    String nama = request.nama().trim();
    String email = request.email().trim().toLowerCase();
    String noTelepon = request.noTelepon().trim();
    String alamat = request.alamat().trim();

    if (userRepository.existsByEmailIgnoreCaseAndDeletedAtIsNull(email)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah digunakan");
    }

    if (userRepository.existsByNoTeleponAndDeletedAtIsNull(noTelepon)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nomor telepon sudah digunakan");
    }

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
}
