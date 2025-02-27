package com.paymybuddy.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = parseJwt(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) { //Check if an authentication object is empty in the SecurityContextHolder
            try {
                // Let Spring Security handle JWT decoding and authentication
                filterChain.doFilter(request, response); //JwtAuthenticationFilter handles authentication
            } catch (Exception e) {
                logger.error("Cannot set user authentication: {}", e);
            }
        } else {
            filterChain.doFilter(request, response); //Continue if no token
        }

    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION); // "Authorization" header

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Extract token after "Bearer "
        }

        return null;
    }
}
