package com.example.FreightGrid.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts a decoded {@link Jwt} into a {@link JwtAuthenticationToken} whose
 * {@link GrantedAuthority} set is derived from the token's {@code roles}
 * (or {@code permissions}) claim.
 *
 * <p><strong>Claim resolution order:</strong></p>
 * <ol>
 *   <li>If a {@code roles} claim exists it is used.</li>
 *   <li>Otherwise, the {@code permissions} claim is tried.</li>
 *   <li>If neither claim is present an empty authority set is returned, which
 *       effectively denies any role-gated endpoint.</li>
 * </ol>
 *
 * <p>Each extracted role value is uppercased and prefixed with {@code ROLE_}
 * (e.g. {@code "analyst"} → {@code ROLE_ANALYST}) so that Spring Security's
 * {@code hasRole("ANALYST")} / {@code @PreAuthorize("hasRole('ANALYST')")}
 * expressions match without additional configuration.</p>
 *
 * <p>Standard {@code SCOPE_} authorities from the JWT's {@code scope} /
 * {@code scp} claim are also included via the default
 * {@link JwtGrantedAuthoritiesConverter} so that scope-based checks remain
 * functional alongside role-based checks.</p>
 */
@Component
public class JwtClaimsConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /** Default Spring converter that extracts SCOPE_ authorities. */
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    // ── Claim key constants ─────────────────────────────────────────────────
    private static final String ROLES_CLAIM       = "roles";
    private static final String PERMISSIONS_CLAIM = "permissions";
    private static final String ROLE_PREFIX       = "ROLE_";

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // 1. Gather default SCOPE_ authorities
        Collection<GrantedAuthority> scopeAuthorities =
                defaultGrantedAuthoritiesConverter.convert(jwt);

        // 2. Extract role-based authorities from custom claims
        Collection<GrantedAuthority> roleAuthorities = extractRoleAuthorities(jwt);

        // 3. Merge both sets into a single immutable list
        List<GrantedAuthority> combinedAuthorities = Stream.concat(
                scopeAuthorities != null ? scopeAuthorities.stream() : Stream.empty(),
                roleAuthorities.stream()
        ).collect(Collectors.toUnmodifiableList());

        return new JwtAuthenticationToken(jwt, combinedAuthorities, jwt.getSubject());
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    /**
     * Resolves role authorities from the JWT.  Handles the claim value being
     * either a {@link Collection} (common with most IdPs) or a plain
     * {@link String} (single-role tokens).
     */
    private Collection<GrantedAuthority> extractRoleAuthorities(Jwt jwt) {
        Object claimValue = jwt.getClaim(ROLES_CLAIM);
        if (claimValue == null) {
            claimValue = jwt.getClaim(PERMISSIONS_CLAIM);
        }
        if (claimValue == null) {
            return Collections.emptyList();
        }

        if (claimValue instanceof Collection<?> roles) {
            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(this::toRoleAuthority)
                    .collect(Collectors.toUnmodifiableList());
        }

        if (claimValue instanceof String singleRole) {
            return List.of(toRoleAuthority(singleRole));
        }

        // Unrecognised claim type – fail safe with no authorities
        return Collections.emptyList();
    }

    /**
     * Uppercases the role name and prepends {@code ROLE_} if not already
     * present.  This guarantees idempotent conversion regardless of how the
     * IdP formats roles.
     */
    private GrantedAuthority toRoleAuthority(String role) {
        String normalised = role.trim().toUpperCase();
        if (!normalised.startsWith(ROLE_PREFIX)) {
            normalised = ROLE_PREFIX + normalised;
        }
        return new SimpleGrantedAuthority(normalised);
    }
}
