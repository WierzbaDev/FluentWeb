package org.wierzbadev.notificationservice.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class JwkService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String jwksUrl;
    private PublicKey cachedPublicKey;


    public JwkService(RestTemplate restTemplate, ObjectMapper objectMapper,
                      @Value("${notification-service.publicKey}") String jwksUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.jwksUrl = jwksUrl;
    }

    public PublicKey getPublicKey() {
        if (cachedPublicKey == null) {
            cachedPublicKey = fetchPublicKey();
        }
        return cachedPublicKey;
    }

    private PublicKey fetchPublicKey() {
        try {
            String response = restTemplate.getForObject(jwksUrl, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            String publicKeyBase64 = jsonNode.get("keys").get(0).get("x5c").get(0).asText();

            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve token form user-service ", e);
        }
    }
}
