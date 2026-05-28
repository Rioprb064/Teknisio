package com.teknisio.services;

import com.teknisio.dto.requests.LoginRequest;
import com.teknisio.dto.requests.LogoutRequest;
import com.teknisio.dto.requests.RefreshTokenRequest;
import com.teknisio.dto.responses.LoginResponse;
import com.teknisio.dto.responses.LogoutResponse;
import com.teknisio.dto.responses.RefreshTokenResponse;
import com.teknisio.model.entities.User;
import com.teknisio.model.entities.UserSession;
import com.teknisio.model.enums.UserStatus;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

  private final UserRepository userRepository;
  private final UserSessionRepository userSessionRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  private final SecureRandom secureRandom = new SecureRandom();

  @Value("${app.refresh-token.expiration-ms:604800000}")
  private long refreshTokenExpirationMs;

  @Transactional
  public LoginResponse login(LoginRequest request) {
    String email = normalizeEmail(request.email());

    User user = userRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Invalid email or password"
      ));

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Invalid email or password"
      );
    }

    if (user.getStatusAkun() != UserStatus.ACTIVE) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Account is not active"
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
        "Invalid refresh token"
      ));

    OffsetDateTime now = OffsetDateTime.now();

    if (session.getExpiredAt().isBefore(now) || session.getExpiredAt().isEqual(now)) {
      session.setRevokedAt(now);
      userSessionRepository.save(session);

      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Refresh token has expired"
      );
    }

    User user = userRepository.findByIdUserAndDeletedAtIsNull(session.getUser().getIdUser())
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Invalid user"
      ));

    if (user.getStatusAkun() != UserStatus.ACTIVE) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Account is not active"
      );
    }

    String newAccessToken = jwtService.generateAccessToken(user);

    return new RefreshTokenResponse(
      newAccessToken,
      "Bearer",
      jwtService.getExpirationMs()
    );
  }

  @Transactional
  public LogoutResponse logout(LogoutRequest request) {
    String refreshToken = request.refreshToken().trim();
    String refreshTokenHash = hashRefreshToken(refreshToken);

    UserSession session = userSessionRepository.findByRefreshTokenHashAndRevokedAtIsNull(refreshTokenHash)
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Invalid session or already logged out"
      ));

    session.setRevokedAt(OffsetDateTime.now());
    userSessionRepository.save(session);

    return new LogoutResponse("Logout successful");
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
      throw new IllegalStateException("SHA-256 is not available", e);
    }
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase();
  }
}
