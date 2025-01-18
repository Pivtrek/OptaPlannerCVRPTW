package com.example.routeplaner.utils;

import jakarta.servlet.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Pobranie tokena z nagłówka Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Jeśli brak tokena, przejdź do następnego filtra
            return;
        }

        // Wyodrębnienie tokena
        String jwtToken = authHeader.substring(7);
        Long userId;

        try {
            // Walidacja tokena i pobranie userId
            userId = Long.valueOf(jwtUtil.validateToken(jwtToken));
        } catch (Exception e) {
            filterChain.doFilter(request, response); // Jeśli token niepoprawny, przejdź dalej
            return;
        }

        // Tworzenie obiektu Authentication z userId
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, null);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Ustawienie kontekstu Spring Security
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
