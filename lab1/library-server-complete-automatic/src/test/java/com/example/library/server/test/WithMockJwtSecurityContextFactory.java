package com.example.library.server.test;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WithMockJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockJwt> {

  @Override
  public SecurityContext createSecurityContext(WithMockJwt withMockJwt) {

    Map<String, Object> claims = new HashMap<>();

    claims.put("sub", withMockJwt.username());
    claims.put("username", withMockJwt.username());
    claims.put("email", withMockJwt.email());
    claims.put("scope", withMockJwt.scopes());

    JwtAuthenticationToken jwtAuthenticationToken =
        new JwtAuthenticationToken(
            new Jwt("test", null, null, Collections.singletonMap("typ", "jwt"), claims),
            Arrays.stream(withMockJwt.scopes())
                .map(sc -> new SimpleGrantedAuthority("SCOPE_" + sc))
                .collect(Collectors.toList()));
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    jwtAuthenticationToken.setAuthenticated(true);
    context.setAuthentication(jwtAuthenticationToken);
    return context;
  }
}
