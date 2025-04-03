package org.wierzbadev.notificationservice.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.wierzbadev.notificationservice.model.dto.auth.AuthRequest;
import org.wierzbadev.notificationservice.model.dto.auth.AuthResponse;

import java.security.PublicKey;
import java.util.Date;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final JwkService jwkService;
    private String cachedToken;
    private String cachedRefreshToken;
    private Date tokenExpiration;

    @Value("${notification-service.authUrl}")
    private String authUrl;

    @Value("${notification-service.refreshUrl}")
    private String refreshUrl;

    @Value("${notification-service.systemUsername}")
    private String systemUsername;

    @Value("${notification-service.systemPassword}")
    private String systemPassword;

    public AuthService(RestTemplate restTemplate, JwkService jwkService) {
        this.restTemplate = restTemplate;
        this.jwkService = jwkService;
    }

    public String getSystemToken() {
        if (cachedToken == null || isTokenExpired()) {
            if (cachedRefreshToken != null) {
                refreshSystemToken();
            } else {
                fetchNewToken();
            }
        }
        return cachedToken;
    }

    public void fetchNewToken() {
        AuthRequest request = AuthRequest.builder()
                .email(systemUsername)
                .password(systemPassword)
                .build();

        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                authUrl, HttpMethod.POST, new HttpEntity<>(request), AuthResponse.class
        );

        if (response.getBody() != null) {
            cachedToken = response.getBody().getAccessToken();
            cachedRefreshToken = response.getBody().getRefreshToken();
            tokenExpiration = extractExpirationDate(cachedToken);
        } else {
            throw new RuntimeException("Failed to retrieve token from user-service");
        }
    }

    private void refreshSystemToken() {
        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                refreshUrl + "?refreshToken=" + cachedRefreshToken,
                HttpMethod.POST,
                new HttpEntity<>(new HttpHeaders()),
                AuthResponse.class
        );

        if (response.getBody() != null) {
            cachedToken = response.getBody().getAccessToken();
            cachedRefreshToken = response.getBody().getRefreshToken();
            tokenExpiration = extractExpirationDate(cachedToken);
        } else {
            fetchNewToken();
        }
    }

    private boolean isTokenExpired() {
        return tokenExpiration == null || tokenExpiration.before(new Date());
    }

    private Date extractExpirationDate(String token) {
        try {
            PublicKey publicKey = jwkService.getPublicKey();

            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration();
        } catch (Exception e) {
            return new Date(0);
        }
    }
}
