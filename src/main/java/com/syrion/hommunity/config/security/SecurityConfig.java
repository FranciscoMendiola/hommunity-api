package com.syrion.hommunity.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.syrion.hommunity.config.jwt.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;

    public SecurityConfig(JwtAuthFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthEntryPoint()))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        // ===========================================================================================

                        // Auth
                        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Rutas con token (Roles específicos)
                        // ===========================================================================================

                        // Rutas de casa
                        .requestMatchers(HttpMethod.GET, "/casa/zona/**").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/casa/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                        .requestMatchers(HttpMethod.POST, "/casa/**").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/casa/**").hasAnyAuthority("ADMINISTRADOR")

                        // Rutas de familia
                        .requestMatchers(HttpMethod.GET, "/familia/zona/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/familia/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                        .requestMatchers(HttpMethod.POST, "/familia/**").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/familia/**").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/familia/**").hasAnyAuthority("ADMINISTRADOR")

                        // Invitado
                        .requestMatchers(HttpMethod.GET, "/invitado").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/invitado/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                        .requestMatchers(HttpMethod.POST, "/invitado/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")

                        // Qr
                        .requestMatchers(HttpMethod.GET, "/qr").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/qr/active").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/qr/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                        .requestMatchers(HttpMethod.POST, "/qr/invitado").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                        .requestMatchers(HttpMethod.POST, "/qr/**").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/qr/residente/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")

                        // Usuario
                        .requestMatchers(HttpMethod.POST, "/usuario").permitAll() // Ruta pública
                        .requestMatchers(HttpMethod.GET, "/usuario/zona/**").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/usuario/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                        .requestMatchers(HttpMethod.GET, "/usuario/estado/pendiente/{idZona}").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/usuario/**").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/usuario/*/estado").hasAnyAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/usuario/*/contraseña").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")

                        // Zona
                        .requestMatchers(HttpMethod.GET, "/zona/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/zona/**").hasAuthority("ADMINISTRADOR")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()

                // .requestMatchers(HttpMethod.GET, "/casa/**").hasAnyAuthority("ADMINISTRADOR",
                // "RESIDENTE")
                // .requestMatchers(HttpMethod.GET,
                // "/familia/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                // .requestMatchers(HttpMethod.POST,
                // "/invitado/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                // .requestMatchers(HttpMethod.GET,
                // "/invitado/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                // .requestMatchers(HttpMethod.GET, "/qr/**").hasAnyAuthority("ADMINISTRADOR",
                // "RESIDENTE")
                // .requestMatchers(HttpMethod.PATCH,
                // "/usuario/**/contraseña").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE")
                // .requestMatchers(HttpMethod.GET,
                // "/usuario/**").hasAnyAuthority("ADMINISTRADOR", "RESIDENTE"))

                // .anyRequest().hasAuthority("ADMINISTRADOR")

                // Filtro JWT}
                ).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
