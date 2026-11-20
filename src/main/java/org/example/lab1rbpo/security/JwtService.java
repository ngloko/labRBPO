package org.example.lab1rbpo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Генерация и валидация JWT (HS256) для access- и refresh-токенов.
 * Время жизни задаётся настройками jwt.access-ttl-minutes / jwt.refresh-ttl-days.
 */
@Component
public class JwtService {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private static final String CLAIM_TYPE = "type";

    private final SecretKey key;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ttl-minutes}") long accessTtlMinutes,
            @Value("${jwt.refresh-ttl-days}") long refreshTtlDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = Duration.ofMinutes(accessTtlMinutes);
        this.refreshTtl = Duration.ofDays(refreshTtlDays);
    }

    public GeneratedToken generateAccessToken(String username) {
        return generate(username, TYPE_ACCESS, accessTtl);
    }

    public GeneratedToken generateRefreshToken(String username) {
        return generate(username, TYPE_REFRESH, refreshTtl);
    }

    private GeneratedToken generate(String username, String type, Duration ttl) {
        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiry = issuedAt.plus(ttl);
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .subject(username)
                .claim(CLAIM_TYPE, type)
                .id(jti)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiry))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        return new GeneratedToken(token, expiry, jti);
    }

    /**
     * Проверяет подпись, срок действия, наличие обязательных claims (sub, type, jti, exp)
     * и что type токена совпадает с ожидаемым.
     *
     * @throws InvalidTokenException если любая из проверок не пройдена
     */
    public Claims validate(String token, String expectedType) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Невалидный токен", e);
        }

        String subject = claims.getSubject();
        String type = claims.get(CLAIM_TYPE, String.class);
        String jti = claims.getId();
        Date expiration = claims.getExpiration();

        if (subject == null || subject.isBlank()
                || type == null
                || jti == null || jti.isBlank()
                || expiration == null) {
            throw new InvalidTokenException("В токене отсутствуют обязательные claims");
        }
        if (!expectedType.equals(type)) {
            throw new InvalidTokenException("Неверный тип токена: ожидался " + expectedType);
        }
        return claims;
    }

    public record GeneratedToken(String token, Instant expiry, String jti) {}
}