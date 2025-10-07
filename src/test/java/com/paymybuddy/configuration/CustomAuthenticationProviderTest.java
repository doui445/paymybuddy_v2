package com.paymybuddy.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomAuthenticationProvider customAuthenticationProvider;

    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userDetails = mock(UserDetails.class);
        authentication = mock(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    @DisplayName("Authenticate - Success")
    void givenCorrectCredentials_whenAuthenticate_thenReturnAuthentication() {
        given(authentication.getName()).willReturn("test@example.com"); // Mock principal
        given(authentication.getCredentials()).willReturn("password"); // Mock credentials
        given(userDetailsService.loadUserByUsername("test@example.com")).willReturn(userDetails); // Mock loading user details
        given(userDetails.getPassword()).willReturn("encodedPassword"); // Mock the encoded password of the user details
        given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true); // Mock verifying the password
        given(userDetails.getAuthorities()).willReturn(null); // No authorities in authentication object

        Authentication result = customAuthenticationProvider.authenticate(authentication);

        assertThat(result).isNotNull();
    }

    @DisplayName("Authenticate - Bad Credentials")
    @Test
    void givenWrongCredentials_whenAuthenticate_thenThrowsBadCredentialsException() {

        given(authentication.getName()).willReturn("test@example.com");
        given(authentication.getCredentials()).willReturn("wrong_password"); // Wrong password
        given(userDetailsService.loadUserByUsername("test@example.com")).willReturn(userDetails);
        given(userDetails.getPassword()).willReturn("encodedPassword");
        given(passwordEncoder.matches("wrong_password", "encodedPassword")).willReturn(false); // Passwords don't match


        assertThrows(BadCredentialsException.class, () -> // Check if a BadCredentialsException is thrown
                customAuthenticationProvider.authenticate(authentication)
        );
    }

}