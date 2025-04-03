package org.wierzbadev.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;

@Configuration
public class RsaKeyConfig {

    @Value("${keystore.file}")
    private String KEYSTORE_FILE;
    @Value("${keystore.password}")
    private String KEYSTORE_PASSWORD;
    @Value("${keystore.alias}")
    private String KEY_ALIAS;

    @Bean
    public KeyPair keyPair() {
        try {
            return loadKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Error while loading RSA key: " + e.getMessage(), e);
        }
    }

    private KeyPair loadKeyPair() throws Exception {
        Resource keystoreResource = new FileSystemResource(KEYSTORE_FILE);
        InputStream keystoreStream = keystoreResource.getInputStream();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keystoreStream, KEYSTORE_PASSWORD.toCharArray());

        Key key = keyStore.getKey(KEY_ALIAS, KEYSTORE_PASSWORD.toCharArray());
        if (!(key instanceof PrivateKey)) {
            throw new IllegalStateException("The private key did not found!");
        }

        Certificate cert = keyStore.getCertificate(KEY_ALIAS);
        PublicKey publicKey = cert.getPublicKey();

        return new KeyPair(publicKey, (PrivateKey) key);
    }
}
