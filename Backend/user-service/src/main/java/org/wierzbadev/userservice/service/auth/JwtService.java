package org.wierzbadev.userservice.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.wierzbadev.userservice.exception.UserError;
import org.wierzbadev.userservice.exception.UserException;

import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RedisTemplate<String, String> redisTemplate;
    private final KeyPair keyPair;
    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(String email, long userId, List<String> roles, boolean verify) {
        if (isUserBanned(email)) {
            throw new UserException(UserError.USER_IS_BANNED);
        }

        return Jwts.builder()
                .claim("sub", email)
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("verify", verify)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(String email, long userId, List<String> roles, boolean verify) {
        if (isUserBanned(email)) {
            throw new UserException(UserError.USER_IS_BANNED);
        }

        String refreshToken = Jwts.builder()
                .claim("sub", email)
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("verify", verify)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();

        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, Duration.ofDays(7));

        return refreshToken;
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long extractId(String token) {
        Claims claims = extractClaims(token);
        Object object = claims.get("userId");

        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        throw new IllegalArgumentException("Invalid ID format in token");
    }

    public List<String> extractRole(String token) {
        Object rolesObj = extractClaims(token).get("roles");

        if (rolesObj instanceof List<?>) {
            return ((List<?>) rolesObj).stream()
                    .filter(Objects::nonNull)
                    .map(String.class::cast)
                    .toList();
        }
        return List.of();
    }

    public String extractEmail(String token) {
        return extractClaims(token).get("sub", String.class);
    }

    public boolean isTokenValid(String token, String email) {
        try {
            return extractEmail(token).equals(email)
                    && !isTokenBlacklisted(token)
                    && isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String refreshToken, String email) {
        try {
            return extractEmail(refreshToken).equals(email)
                    && !isTokenBlacklisted(refreshToken)
                    && isTokenExpired(refreshToken);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return !expiration.before(new Date());
    }


    public void blacklistToken(String token) {
        redisTemplate.opsForValue().set("blacklist:token:" + token, "blacklisted", Duration.ofHours(1));
    }

    public void blacklistRefreshToken(String email) {
        String refreshTokenKey = "refresh:" + email;
        if (redisTemplate.hasKey(refreshTokenKey)) {
            String refreshToken = redisTemplate.opsForValue().get(refreshTokenKey);
            if (refreshToken != null) {
                redisTemplate.opsForValue().set("blacklist:token:" + refreshToken, "blacklisted", Duration.ofDays(7));
            }
            redisTemplate.delete(refreshTokenKey);
        }
    }

    public void banUser(String email) {
        redisTemplate.opsForValue().set("blacklist:user:" + email, "banned", Duration.ofDays(7));
        blacklistRefreshToken(email);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:token:" + token));
    }

    public boolean isUserBanned(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:user:" + email));
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
}
