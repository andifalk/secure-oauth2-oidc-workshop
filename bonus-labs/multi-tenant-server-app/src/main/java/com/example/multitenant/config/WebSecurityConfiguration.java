package com.example.multitenant.config;

import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final BearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
  private final String auth0JwkSetUri;
  private final String oktaJwkSetUri;

  public WebSecurityConfiguration(
      @Value("${auth0.jwk-set-uri}") String auth0JwkSetUri,
      @Value("${okta.jwk-set-uri}") String oktaJwkSetUri) {
    this.auth0JwkSetUri = auth0JwkSetUri;
    this.oktaJwkSetUri = oktaJwkSetUri;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests(ar -> ar.anyRequest().authenticated())
        .oauth2ResourceServer()
        .authenticationManagerResolver(multiTenantAuthenticationManager());
  }

  @Bean
  AuthenticationManagerResolver<HttpServletRequest> multiTenantAuthenticationManager() {
    Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
    authenticationManagers.put("https://access-me.eu.auth0.com/", auth0());
    authenticationManagers.put(
        "https://dev-667216.oktapreview.com/oauth2/auskfyzkaoXSRnwTV0h7", okta());
    return request -> {
      String tenantId = toTenant(request);
      return Optional.ofNullable(tenantId)
          .map(authenticationManagers::get)
          .orElseThrow(() -> new IllegalArgumentException("unknown tenant " + tenantId));
    };
  }

  String toTenant(HttpServletRequest request) {
    try {
      return JWTParser.parse(this.bearerTokenResolver.resolve(request))
          .getJWTClaimsSet()
          .getIssuer();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  AuthenticationManager auth0() {
    JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.auth0JwkSetUri).build();
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    authenticationProvider.setJwtAuthenticationConverter(
        new JwtBearerTokenAuthenticationConverter());
    return authenticationProvider::authenticate;
  }

  AuthenticationManager okta() {
    JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(this.oktaJwkSetUri).build();
    JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
    authenticationProvider.setJwtAuthenticationConverter(
        new JwtBearerTokenAuthenticationConverter());
    return authenticationProvider::authenticate;
  }
}
