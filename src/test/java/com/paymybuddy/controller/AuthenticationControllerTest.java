package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.model.dto.AuthRequestDTO;
import com.paymybuddy.model.dto.AuthResponseDTO;
import com.paymybuddy.model.dto.UserRegistrationDTO;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationController authenticationController;

    private UserRegistrationDTO userRegistrationDto;

    @BeforeEach
    void setUp() {
        userRegistrationDto = new UserRegistrationDTO("test", "test@example.com", "password");
    }

    @Test
    @DisplayName("User Login - Success")
    void givenValidCredentials_whenLogin_thenReturnToken() {
        AuthRequestDTO authRequest = new AuthRequestDTO("test@example.com", "password");
        Authentication authentication = mock(Authentication.class); // Mock authentication
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(authentication);
        given(tokenService.generateToken(authentication)).willReturn("testToken");

        ResponseEntity<?> response = authenticationController.login(authRequest); // Call controller method

        assertThat(response.getStatusCode().value()).isEqualTo(200); // Status is OK
        assertThat(response.getBody()).isEqualTo(new AuthResponseDTO("testToken")); // Body is AuthResponse with token generated
    }

    @Test
    @DisplayName("User Login - Bad Credentials")
    void givenInvalidCredentials_whenLogin_thenReturnUnauthorized() {
        AuthRequestDTO authRequest = new AuthRequestDTO("test@example.com", "wrongpassword");
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willThrow(new BadCredentialsException("Bad credentials")); // Throw exception

        ResponseEntity<?> response = authenticationController.login(authRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("User Registration - Success")
    void givenValidUserDto_whenRegister_thenReturnSuccessResponse() {
        BindingResult bindingResult = mock(BindingResult.class); // Mock BindingResult
        given(bindingResult.hasErrors()).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        ResponseEntity<?> response = authenticationController.register(userRegistrationDto, bindingResult);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("User registered successfully!");
        verify(userService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("User Registration - Duplicate Email")
    void givenDuplicateEmail_whenRegisterUser_thenReturnBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class); // Mock BindingResult
        given(bindingResult.hasErrors()).willReturn(false);
        given(userService.saveUser(any())).willThrow(new IllegalArgumentException("Email address already in use."));

        ResponseEntity<?> response = authenticationController.register(userRegistrationDto, bindingResult);

        assertThat(response.getStatusCode().value()).isEqualTo(400); //Bad request
        assertThat(response.getBody()).isEqualTo("Email address already in use.");
    }

    @Test
    @DisplayName("User Registration - Validation Error")
    void givenInvalidUserDto_whenRegister_thenReturnBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class); //BindingResult mock
        given(bindingResult.hasErrors()).willReturn(true); //Validation fails

        ResponseEntity<?> response = authenticationController.register(userRegistrationDto, bindingResult);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

}