package com.syrion.hommunity_api.config.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.syrion.hommunity_api.exception.ApiException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            try {
                if (jwtUtil.validateToken(jwt)) {
                    String username = jwtUtil.extractUsername(jwt);
                    Claims claims = jwtUtil.extractAllClaims(jwt);

                    // Obtener roles y manejar el caso null
                    List<String> roles = claims.get("authorities", List.class);
                    if (roles == null) {
                        roles = new ArrayList<>();
                        logger.warn("No roles found in token for user: {}", username);
                    } else {
                        logger.info("Roles extracted for user {}: {}", username, roles);
                    }

                    var authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Invalid JWT token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Invalid token: " + e.getMessage());
            }
        } else {
            throw new ServletException("Authorization header is missing or invalid format");
        }

        filterChain.doFilter(request, response);
    }
}