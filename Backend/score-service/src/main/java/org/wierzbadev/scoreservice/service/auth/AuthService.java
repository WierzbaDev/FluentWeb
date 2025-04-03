package org.wierzbadev.scoreservice.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.wierzbadev.scoreservice.model.dto.auth.AuthRequest;
import org.wierzbadev.scoreservice.model.dto.auth.AuthResponse;

import java.security.PublicKey;
import java.util.Date;

@Slf4j
@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final JwkService jwkService;
    private String cachedToken;
    private String cachedRefreshToken;
    private Date tokenExpiration;

    @Value("${user-service.auth-url}")
    private String authUrl;

    @Value("${user-service.refresh-url}")
    private String refreshUrl;

    @Value("${user-service.auth-username}")
    private String systemUsername;

    @Value("${user-service.auth-password}")
    private String systemPassword;

    @Value("${app.current-url}")
    private String frontendUrl;

    public AuthService(RestTemplate restTemplate, JwkService jwkService) {
        this.restTemplate = restTemplate;
        this.jwkService = jwkService;
    }

    public String getSystemToken() {
        if (cachedToken == null || isTokenExpired()) {
            log.info("Cached token: {}", cachedToken);
            if (cachedRefreshToken != null) {
                refreshSystemToken();
            } else {
                fetchNewToken();
            }
        }
        log.info("[getSystemToken] Returned cached token");
        return cachedToken;
    }

    private void fetchNewToken() {
        AuthRequest request = AuthRequest.builder()
                .email(systemUsername)
                .password(systemPassword)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin(frontendUrl);

        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                authUrl, HttpMethod.POST, new HttpEntity<>(request, headers), AuthResponse.class
        );

        log.info("[fetch new token] retrieve info: {}", response.getBody());

        if (response.getBody() != null) {
            cachedToken = response.getBody().getAccessToken();
            cachedRefreshToken = response.getBody().getRefreshToken();
            tokenExpiration = extractExpirationDate(cachedToken);
        } else {
            throw new RuntimeException("Failed to retrieve token from user-service");
        }
        log.info("System received system-token from user-service");
    }

    private void refreshSystemToken() {
        log.info("Refreshing token...");
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin(frontendUrl);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<AuthResponse> response = restTemplate.exchange(
                refreshUrl + "?refreshToken=" + cachedRefreshToken,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                AuthResponse.class
        );

        if (response.getBody() != null) {
            cachedToken = response.getBody().getAccessToken();
            cachedRefreshToken = response.getBody().getRefreshToken();
            tokenExpiration = extractExpirationDate(cachedToken);
        } else {
            fetchNewToken();
        }
        log.info("System received refresh system-token from user-service");
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
