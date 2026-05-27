package com.teknisio.security;

import com.teknisio.model.entities.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

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
            .subject(user.getIdUser().toString())
            .claims(claims)
            .issuedAt(now)
            .expiration(expiredAt)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET minimal harus 32 karakter");
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
