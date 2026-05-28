package com.teknisio.security;

import com.teknisio.model.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-ms}")
  private long jwtExpirationMs;

  public String generateAccessToken(User user) {
    Date now = new Date();
    Date expiredAt = new Date(now.getTime() + jwtExpirationMs);

    Map<String, Object> claims = Map.of(
      "email", user.getEmail(),
      "role", user.getRole().name()
    );

    return Jwts.builder()
      .claims(claims)
      .subject(user.getIdUser().toString())
      .issuedAt(now)
      .expiration(expiredAt)
      .signWith(getSigningKey(), Jwts.SIG.HS256)
      .compact();
  }

  public boolean isTokenValid(String token) {
    try {
      getClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public UUID extractUserId(String token) {
    String subject = getClaims(token).getSubject();
    return UUID.fromString(subject);
  }

  public long getExpirationMs() {
    return jwtExpirationMs;
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSigningKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

    if (keyBytes.length < 32) {
      throw new IllegalStateException("JWT_SECRET minimal harus 32 karakter");
    }

    return Keys.hmacShaKeyFor(keyBytes);
  }
}
