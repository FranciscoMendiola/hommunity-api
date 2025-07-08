package com.syrion.hommunity_api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.syrion.hommunity_api.config.jwt.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .exceptionHandling(exception -> exception
            .authenticationEntryPoint(new CustomAuthEntryPoint()))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Usuario
                .requestMatchers(HttpMethod.POST, "/usuario").permitAll()

                // Rutas protegidas (solo con roles específicos)
                .requestMatchers(HttpMethod.GET, "/usuario/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")

                // Zona
                .requestMatchers(HttpMethod.POST, "/zona/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/zona/**").permitAll()

                // Invitado
                .requestMatchers(HttpMethod.GET, "/invitado/**").hasAnyAuthority("RESIDENTE", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/invitado").hasAnyAuthority("RESIDENTE", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/invitado/**").hasAuthority("ADMINISTRADOR")


                // Rutas de casa
                .requestMatchers(HttpMethod.POST, "/casa/**").hasAnyAuthority("ADMINISTRADOR")

                .requestMatchers(HttpMethod.DELETE, "/casa/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")

                // Rutas de familia
                .requestMatchers(HttpMethod.POST, "/familia/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")

                .requestMatchers(HttpMethod.DELETE, "/familia/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")

                // Familias por zona
                .requestMatchers(HttpMethod.GET, "/familia/zona/**").permitAll()

                // Rutas de QR
                .requestMatchers(HttpMethod.POST, "/qr/residente").hasAnyAuthority("RESIDENTE")

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            // Filtro JWT
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

