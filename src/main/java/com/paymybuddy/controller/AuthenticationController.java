package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.model.dto.AuthRequestDTO;
import com.paymybuddy.model.dto.AuthResponseDTO;
import com.paymybuddy.model.dto.UserRegistrationDto;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            // Generate token
            String token = tokenService.generateToken(authentication);
            return ResponseEntity.ok(new AuthResponseDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegistrationDto userDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){ //Input validation
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors().toString());
        }
        try {
            User user = User.builder()
                    .username(userDto.username())
                    .email(userDto.email())
                    .password(passwordEncoder.encode(userDto.password()))
                    .balance(BigDecimal.valueOf(0))
                    .build();
            userService.saveUser(user);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalArgumentException e) {  // Handle duplicate email
            return ResponseEntity.badRequest().body(e.getMessage()); // Return 400 Bad Request with an error message
        }
    }

}
