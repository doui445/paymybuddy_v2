package com.paymybuddy.configuration.rsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfiguration {

    private final RsaKeyProperties rsaKeys;

    @Autowired
    public RsaKeyConfiguration(RsaKeyProperties rsaKeyProperties) {
        this.rsaKeys = rsaKeyProperties;
    }

    @Bean
    public RSAPublicKey publicKey() throws Exception {
        try (InputStream is = rsaKeys.publicKey().getInputStream()) {
            String publicKeyPEM = new String(FileCopyUtils.copyToByteArray(is));
            return getPublicKeyFromPem(publicKeyPEM);
        }
    }

    @Bean
    public RSAPrivateKey privateKey() throws Exception {
        try (InputStream is = rsaKeys.privateKey().getInputStream()) {
            String privateKeyPEM = new String(FileCopyUtils.copyToByteArray(is), StandardCharsets.UTF_8);
            return getPrivateKeyFromPem(privateKeyPEM);
        }
    }

    private RSAPublicKey getPublicKeyFromPem(String publicKeyPEM) throws Exception {
        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Remove all whitespace

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPEM);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) kf.generatePublic(spec);
    }

    private RSAPrivateKey getPrivateKeyFromPem(String privateKeyPEM) throws Exception {
        privateKeyPEM = privateKeyPEM
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", ""); // Remove all whitespace

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) kf.generatePrivate(spec);
    }

}
