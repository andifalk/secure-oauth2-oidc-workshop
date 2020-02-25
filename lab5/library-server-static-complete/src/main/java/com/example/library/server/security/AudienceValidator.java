package com.example.library.server.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/** Validator for expected audience in access tokens. */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

  private OAuth2Error error =
      new OAuth2Error("invalid_token", "The required audience 'library-service' is missing", null);

  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    if (jwt.getAudience().contains("library-service")) {
      return OAuth2TokenValidatorResult.success();
    } else {
      return OAuth2TokenValidatorResult.failure(error);
    }
  }
}
