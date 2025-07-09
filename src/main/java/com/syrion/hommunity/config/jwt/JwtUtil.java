package com.syrion.hommunity.config.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.syrion.hommunity.api.entity.Usuario;
import com.syrion.hommunity.common.util.ConverterRol;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "miClaveUltraSecretaQueDebeSerLarga1234567890";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 dia
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Autowired
    private ConverterRol converterRol;

    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        // Manejar caso donde idRol es null
        claims.put("authorities", List.of(converterRol.getNombreRol(usuario.getIdRol())));
        claims.put("id", usuario.getIdUsuario());
        claims.put("estado", usuario.getEstado());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getCorreo())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            if (claims.get("authorities") == null) {
                throw new JwtException("Token no contiene el campo 'authorities'");
            }
            return true;
        } catch (JwtException e) {
            throw new JwtException("Token inválido o expirado", e);
        }
    }
}