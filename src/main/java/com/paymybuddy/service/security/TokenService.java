package com.paymybuddy.service.security;

import org.springframework.security.core.Authentication;

public interface TokenService {

    String generateToken(Authentication authentication);

}
