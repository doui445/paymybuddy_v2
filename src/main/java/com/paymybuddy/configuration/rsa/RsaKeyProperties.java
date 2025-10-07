package com.paymybuddy.configuration.rsa;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeyProperties(Resource publicKey, Resource privateKey) {
}
