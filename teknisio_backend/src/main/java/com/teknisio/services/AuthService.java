package com.teknisio.services;

import com.teknisio.dto.requests.LoginRequest;
import com.teknisio.dto.requests.RefreshTokenRequest;
import com.teknisio.dto.requests.RegisterCustomerRequest;
import com.teknisio.dto.requests.RegisterTeknisiRequest;
import com.teknisio.dto.responses.AuthProfileResponse;
import com.teknisio.dto.responses.CustomerAuthProfileResponse;
import com.teknisio.dto.responses.LoginResponse;
import com.teknisio.dto.responses.RefreshTokenResponse;
import com.teknisio.dto.responses.RegisterCustomerResponse;
import com.teknisio.dto.responses.RegisterTeknisiResponse;
import com.teknisio.dto.responses.TeknisiAuthProfileResponse;
import com.teknisio.dto.requests.LogoutRequest;
import com.teknisio.dto.responses.LogoutResponse;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.entities.User;
import com.teknisio.model.entities.UserSession;
import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.TeknisiProfileRepository;
import com.teknisio.repositories.UserRepository;
import com.teknisio.repositories.UserSessionRepository;
import com.teknisio.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final TeknisiProfileRepository teknisiProfileRepository;
  private final UserSessionRepository userSessionRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  private final SecureRandom secureRandom = new SecureRandom();

  @Value("${app.refresh-token.expiration-ms:604800000}")
  private long refreshTokenExpirationMs;

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
      .build();

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
      .build();

    User savedUser = userRepository.save(user);

    TeknisiProfile teknisiProfile = TeknisiProfile.builder()
      .user(savedUser)
      .statusKetersediaan(TeknisiStatus.OFFLINE)
      .ratingAvg(BigDecimal.ZERO)
      .ratingCount(0)
      .totalPekerjaan(0)
      .deskripsi(deskripsi)
      .build();

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

  @Transactional
  public LoginResponse login(LoginRequest request) {
    String email = normalizeEmail(request.email());

    User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Email atau password salah"
      ));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Email atau password salah"
      );
    }

    if (user.getStatusAkun() != UserStatus.ACTIVE) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Akun tidak aktif"
      );
    }

    OffsetDateTime now = OffsetDateTime.now();

    user.setLastLogin(now);
    userRepository.save(user);

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = generateRefreshToken();
    String refreshTokenHash = hashRefreshToken(refreshToken);

    UserSession session = UserSession.builder()
      .user(user)
      .refreshTokenHash(refreshTokenHash)
      .expiredAt(now.plus(Duration.ofMillis(refreshTokenExpirationMs)))
      .build();

    userSessionRepository.save(session);

    return new LoginResponse(
      accessToken,
      refreshToken,
      "Bearer",
      jwtService.getExpirationMs(),
      refreshTokenExpirationMs,
      user.getIdUser(),
      user.getNama(),
      user.getEmail(),
      user.getNoTelepon(),
      user.getRole(),
      user.getStatusAkun()
    );
  }

  @Transactional
  public RefreshTokenResponse refresh(RefreshTokenRequest request) {
    String refreshToken = request.refreshToken().trim();
    String refreshTokenHash = hashRefreshToken(refreshToken);

    UserSession session = userSessionRepository.findByRefreshTokenHashAndRevokedAtIsNull(refreshTokenHash)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Refresh token tidak valid"
      ));

    OffsetDateTime now = OffsetDateTime.now();

    if (session.getExpiredAt().isBefore(now) || session.getExpiredAt().isEqual(now)) {
      session.setRevokedAt(now);
      userSessionRepository.save(session);

      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Refresh token sudah expired"
      );
    }

    User user = userRepository.findByIdUserAndDeletedAtIsNull(session.getUser().getIdUser())
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "User tidak valid"
      ));

    if (user.getStatusAkun() != UserStatus.ACTIVE) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Akun tidak aktif"
      );
    }

    String newAccessToken = jwtService.generateAccessToken(user);

    return new RefreshTokenResponse(
      newAccessToken,
      "Bearer",
      jwtService.getExpirationMs()
    );
  }

  @Transactional(readOnly = true)
  public AuthProfileResponse profile(User authenticatedUser) {
    User user = userRepository.findByIdUserAndDeletedAtIsNull(authenticatedUser.getIdUser())
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "User tidak valid"
      ));

    if (user.getStatusAkun() != UserStatus.ACTIVE) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Akun tidak aktif"
      );
    }

    if (user.getRole() == UserRole.TEKNISI) {
      TeknisiProfile profile = teknisiProfileRepository.findByUser_IdUser(user.getIdUser())
        .orElseThrow(() -> new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Profil teknisi tidak ditemukan"
        ));

      return new TeknisiAuthProfileResponse(
        user.getIdUser(),
        profile.getIdTeknisiProfile(),
        user.getNama(),
        user.getEmail(),
        user.getNoTelepon(),
        user.getAlamat(),
        user.getRole(),
        user.getStatusAkun(),
        profile.getStatusKetersediaan(),
        profile.getRatingAvg(),
        profile.getRatingCount(),
        profile.getTotalPekerjaan(),
        profile.getDeskripsi(),
        user.getFotoProfil()
      );
    }

    return new CustomerAuthProfileResponse(
      user.getIdUser(),
      user.getNama(),
      user.getEmail(),
      user.getNoTelepon(),
      user.getAlamat(),
      user.getRole(),
      user.getStatusAkun(),
      user.getFotoProfil()
    );
  }

  @Transactional
  public LogoutResponse logout(LogoutRequest request) {
    String refreshToken = request.refreshToken().trim();
    String refreshTokenHash = hashRefreshToken(refreshToken);

    UserSession session = userSessionRepository.findByRefreshTokenHashAndRevokedAtIsNull(refreshTokenHash)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Session tidak valid atau sudah logout"
      ));

    session.setRevokedAt(OffsetDateTime.now());
    userSessionRepository.save(session);

    return new LogoutResponse("Logout berhasil");
  }

  private String generateRefreshToken() {
    byte[] randomBytes = new byte[64];
    secureRandom.nextBytes(randomBytes);

    return Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(randomBytes);
  }

  private String hashRefreshToken(String refreshToken) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));

      return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 tidak tersedia", e);
    }
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
