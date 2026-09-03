package com.example.FreightGrid.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OAuth 2.0 Resource Server security configuration for FreightGrid.
 *
 * <h3>Design decisions</h3>
 * <ul>
 *   <li><strong>Stateless sessions</strong> – the API is token-based; no
 *       server-side session is ever created, eliminating CSRF risk and
 *       aligning with 12-factor principles.</li>
 *   <li><strong>CSRF disabled</strong> – safe because no cookies or
 *       browser-managed credentials are used; every request carries a
 *       Bearer JWT in the {@code Authorization} header.</li>
 *   <li><strong>All endpoints authenticated</strong> – the default
 *       deny-all posture ensures that new endpoints are secure by
 *       default until explicitly opened.</li>
 *   <li><strong>Method-level security</strong> – {@code @EnableMethodSecurity}
 *       allows controllers and services to use {@code @PreAuthorize} for
 *       fine-grained role/authority checks (e.g.
 *       {@code @PreAuthorize("hasRole('DIRECTOR')")}).</li>
 *   <li><strong>Custom JWT converter</strong> – the {@link JwtClaimsConverter}
 *       maps IdP-specific claims ({@code roles} / {@code permissions})
 *       into Spring Security {@code ROLE_*} authorities.</li>
 * </ul>
 *
 * @see JwtClaimsConverter
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity                       // enables @PreAuthorize / @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtClaimsConverter jwtClaimsConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // ── 1. Disable CSRF – stateless JWT API, no cookie auth ────────
            .csrf(csrf -> csrf.disable())

            // ── 2. Stateless session management ────────────────────────────
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ── 3. Require authentication on every endpoint by default ─────
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/api/**").permitAll()
                            .anyRequest().authenticated()
            )

            // ── 4. Configure OAuth2 Resource Server with custom JWT
            //       converter so roles are mapped to GrantedAuthorities ──────
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtClaimsConverter)
                )
            );

        return http.build();
    }
}
