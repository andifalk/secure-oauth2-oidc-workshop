package com.example.library.server.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/** JWT converter that takes the roles from 'groups' claim of JWT token. */
@SuppressWarnings("unused")
public class LibraryUserJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {
  private static final String GROUPS_CLAIM = "groups";
  private static final String ROLE_PREFIX = "ROLE_";

  private final LibraryUserDetailsService libraryUserDetailsService;

  public LibraryUserJwtAuthenticationConverter(
      LibraryUserDetailsService libraryUserDetailsService) {
    this.libraryUserDetailsService = libraryUserDetailsService;
  }

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
    return Optional.ofNullable(
            libraryUserDetailsService.loadUserByUsername(jwt.getClaimAsString("email")))
        .map(u -> new UsernamePasswordAuthenticationToken(u, "n/a", authorities))
        .orElseThrow(() -> new BadCredentialsException("No user found"));
  }

  private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
    return this.getGroups(jwt).stream()
        .map(authority -> ROLE_PREFIX + authority.toUpperCase())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private Collection<String> getGroups(Jwt jwt) {
    Object groups = jwt.getClaims().get(GROUPS_CLAIM);
    if (groups instanceof Collection) {
      return (Collection<String>) groups;
    }

    return Collections.emptyList();
  }
}
