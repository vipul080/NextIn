package com.vipul.nextin.security;

import com.vipul.nextin.entity.Role;
import com.vipul.nextin.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

// Creates and checks JWTs. A token is signed with HMAC-SHA256 using JWT_SECRET,
// so anyone can read its contents but nobody without the secret can change them.
@Service
public class JwtService {

    private final SecretKey key;
    private final Duration lifetime;
    private final Clock clock;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes,
                      Clock clock) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.lifetime = Duration.ofMinutes(expirationMinutes);
        this.clock = clock;
    }

    public long lifetimeSeconds() {
        return lifetime.toSeconds();
    }

    public String issue(User user) {
        Instant now = Instant.now(clock);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .claim("clinicId", user.getClinicId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(lifetime)))
                .signWith(key)
                .compact();
    }

    // Throws JwtException if the signature is wrong, the token was changed, or it has expired.
    public AuthUser parse(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Number clinicId = claims.get("clinicId", Number.class);
        return new AuthUser(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                Role.valueOf(claims.get("role", String.class)),
                clinicId == null ? null : clinicId.longValue());
    }
}
