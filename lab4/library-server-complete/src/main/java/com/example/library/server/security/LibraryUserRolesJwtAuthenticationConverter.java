package com.example.library.server.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

/** JWT converter that takes the roles from persistent user roles. */
@SuppressWarnings("unused")
public class LibraryUserRolesJwtAuthenticationConverter
    implements Converter<Jwt, AbstractAuthenticationToken> {

  private final LibraryUserDetailsService libraryUserDetailsService;

  public LibraryUserRolesJwtAuthenticationConverter(
      LibraryUserDetailsService libraryUserDetailsService) {
    this.libraryUserDetailsService = libraryUserDetailsService;
  }

  @Override
  public AbstractAuthenticationToken convert(Jwt jwt) {
    return Optional.ofNullable(libraryUserDetailsService.loadUserByUsername(jwt.getSubject()))
        .map(u -> new UsernamePasswordAuthenticationToken(u, "n/a", u.getAuthorities()))
        .orElseThrow(() -> new BadCredentialsException("User not found"));
  }
}
