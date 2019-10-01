package com.example.library.server.test;

import com.example.library.server.dataaccess.User;
import com.example.library.server.security.LibraryUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WithMockLibraryUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockLibraryUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockLibraryUser withMockLibraryUser) {

    List<GrantedAuthority> grantedAuthorities =
        Arrays.stream(withMockLibraryUser.roles())
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
            .collect(Collectors.toList());

    LibraryUser libraryUser =
        new LibraryUser(
            new User(
                UUID.randomUUID(),
                "user",
                "user",
                "user",
                Arrays.asList(withMockLibraryUser.roles())));

    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
        new UsernamePasswordAuthenticationToken(libraryUser, "password", grantedAuthorities);
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(usernamePasswordAuthenticationToken);
    return context;
  }
}
