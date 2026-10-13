package com.labrasaviva.config;

import com.labrasaviva.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                )
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/platos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/menu/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reservas").permitAll()

                        // Platos: creación, modificación y eliminación exclusivas de Administrador
                        .requestMatchers(HttpMethod.POST, "/api/platos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/platos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/platos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/platos/**").hasAnyRole("ADMIN", "COCINA")

                        // Pedidos: Mesero, Cocina y Admin
                        .requestMatchers("/api/pedidos/**").hasAnyRole("MESERO", "COCINA", "ADMIN")

                        // Cuentas: Mesero, Cajero y Admin
                        .requestMatchers("/api/cuentas/**").hasAnyRole("MESERO", "CAJERO", "ADMIN")

                        // Reservas (consulta y gestión interna): Mesero y Admin
                        .requestMatchers(HttpMethod.GET, "/api/reservas/**").hasAnyRole("MESERO", "ADMIN")

                        // Parqueadero: Operador de parqueadero y Admin
                        .requestMatchers("/api/parqueadero/**").hasAnyRole("PARQUEADERO", "ADMIN")

                        // Auditoría de eventos NoSQL: Administrador
                        .requestMatchers("/api/v1/auditoria/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
