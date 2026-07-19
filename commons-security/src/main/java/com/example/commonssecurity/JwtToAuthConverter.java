package com.example.commonssecurity;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;

public class JwtToAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<String> scopes = new LinkedHashSet<>();

        Object permissions = jwt.getClaims().get("permissions");
        if(permissions instanceof Collection<?>) {
            for(Object permission : (Collection<?>)permissions) {
            scopes.add(String.valueOf(permission));
            }
        }

        String scope = jwt.getClaimAsString("scope");
        if(scope != null && !scope.isBlank()) {
            scopes.addAll(Arrays.asList(scope.split(" ")));
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for(final String s: scopes) {
            grantedAuthorities.add(() -> "SCOPE_" + s);
        }
        return new JwtAuthenticationToken(jwt, grantedAuthorities);
    }

}
