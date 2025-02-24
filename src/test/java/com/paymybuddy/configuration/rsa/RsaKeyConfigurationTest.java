package com.paymybuddy.configuration.rsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RsaKeyConfigurationTest {

    @Mock
    private RsaKeyProperties rsaKeyProperties;

    @InjectMocks
    private RsaKeyConfiguration rsaKeyConfiguration;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Public key - Valid key - Returns public key")
    void publicKey_validKey_returnsPublicKey() throws Exception {
        Resource mockPublicKeyResource = new ClassPathResource("certs/public_key.pem");
        given(rsaKeyProperties.publicKey()).willReturn(mockPublicKeyResource);

        RSAPublicKey publicKey = rsaKeyConfiguration.publicKey();

        assertThat(publicKey).isNotNull();
    }

    @Test
    @DisplayName("Public key - Invalid key - Throws exception")
    void publicKey_invalidKey_throwsException() {
        Resource mockPublicKeyResource = new ClassPathResource("invalid_public_key.pem");//Non existent/invalid key
        given(rsaKeyProperties.publicKey()).willReturn(mockPublicKeyResource);

        assertThrows(Exception.class, () -> rsaKeyConfiguration.publicKey());
    }

    @Test
    @DisplayName("Private key - Valid key - Returns private key")
    void privateKey_validKey_returnsPrivateKey() throws Exception {
        Resource mockPrivateKeyResource = new ClassPathResource("certs/private_key_pkcs8.pem"); //Provide valid private key
        given(rsaKeyProperties.privateKey()).willReturn(mockPrivateKeyResource);

        RSAPrivateKey privateKey = rsaKeyConfiguration.privateKey();

        assertThat(privateKey).isNotNull();
    }

    @Test
    @DisplayName("Private key - Invalid key - Throws exception")
    void privateKey_invalidKey_throwsException() {
        Resource mockPrivateKeyResource = new ClassPathResource("invalid_private_key.pem"); //Non existent or invalid
        given(rsaKeyProperties.privateKey()).willReturn(mockPrivateKeyResource);

        assertThrows(Exception.class, () -> rsaKeyConfiguration.privateKey());
    }

}