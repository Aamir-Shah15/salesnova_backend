package com.kodnest.salesnova.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * ─────────────────────────────────────────────────────────────────
 *  CartNest — Security + CORS Configuration
 *
 *  IMPORTANT: Do NOT create a separate CorsConfig.java.
 *  When Spring Security is present, it intercepts requests before
 *  any CorsFilter bean runs. CORS must be configured here, inside
 *  the SecurityFilterChain, so Spring Security handles it correctly.
 * ─────────────────────────────────────────────────────────────────
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ── All origins allowed to call this backend ──────────────────────────────
    private static final List<String> ALLOWED_ORIGINS = List.of(
        "http://localhost:5173",                                           // Vite dev
        "http://localhost:3000",                                           // alt dev
        "http://localhost:8080",                                           // your dev
        "https://cartnest-sigma.vercel.app",                              // production
        "https://cartnest-git-master-shaikh-shazebs-projects.vercel.app"  // preview
    );

    // ── 1. Security filter chain ──────────────────────────────────────────────
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Hand CORS to our corsConfigurationSource() bean below
            // This is the critical line — NOT http.cors() with no arguments
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Disable CSRF — REST APIs are stateless, no cookie sessions
            .csrf(csrf -> csrf.disable())

            // No server-side sessions
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Route permissions
            .authorizeHttpRequests(auth -> auth
                // OPTIONS must always be permitted — this is the browser preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public endpoints — no token needed
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users/register").permitAll()

                // Lock everything else behind authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // ── 2. CORS rules ─────────────────────────────────────────────────────────
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Exact origins — wildcards cannot be used when allowCredentials is true
        config.setAllowedOrigins(ALLOWED_ORIGINS);

        // All HTTP methods including OPTIONS (preflight)
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Headers the browser sends with a JSON request
        config.setAllowedHeaders(List.of(
            "Content-Type",
            "Authorization",
            "Accept",
            "Origin",
            "X-Requested-With"
        ));

        // Required if you ever send cookies or Authorization headers
        config.setAllowCredentials(true);

        // Browser caches the preflight response for 1 hour
        config.setMaxAge(3600L);

        // Apply this config to every route
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
