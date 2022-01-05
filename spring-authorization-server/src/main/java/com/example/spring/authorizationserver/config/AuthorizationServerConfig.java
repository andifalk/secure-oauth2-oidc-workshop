package com.example.spring.authorizationserver.config;

import com.example.spring.authorizationserver.jose.Jwks;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        return http.formLogin(Customizer.withDefaults()).build();
    }

    // @formatter:off
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        RegisteredClient demoClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("demo-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethods(methods -> methods.addAll(
                        List.of(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                                ClientAuthenticationMethod.CLIENT_SECRET_POST
                        )))
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:9095/client/callback")
                .redirectUri("http://127.0.0.1:9095/client/authorized")
                .redirectUri("http://127.0.0.1:9095/client")
                .scopes(scopes -> scopes.addAll(List.of(
                        OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL, "offline_access"
                )))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();

        RegisteredClient demoClientPkce = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("demo-client-pkce")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:9095/client/callback")
                .redirectUri("http://127.0.0.1:9095/client/authorized")
                .redirectUri("http://127.0.0.1:9095/client")
                .scopes(scopes -> scopes.addAll(List.of(
                        OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL, "offline_access"
                )))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .build();

        RegisteredClient demoClientConsent = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("demo-client-consent")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethods(methods -> methods.addAll(
                        List.of(
                                ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                                ClientAuthenticationMethod.CLIENT_SECRET_POST
                        )))
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:9095/client/callback")
                .redirectUri("http://127.0.0.1:9095/client/authorized")
                .redirectUri("http://127.0.0.1:9095/client")
                .scopes(scopes -> scopes.addAll(List.of(
                        OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL, "offline_access"
                )))
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        // Save registered client in db as if in-memory
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        registeredClientRepository.save(demoClient);
        registeredClientRepository.save(demoClientPkce);
        registeredClientRepository.save(demoClientConsent);

        return registeredClientRepository;
    }
    // @formatter:on

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://auth-server:9000").build();
    }

    @Bean
    public EmbeddedDatabase embeddedDatabase() {
        // @formatter:off
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/oauth2-authorization-consent-schema.sql")
                .addScript("org/springframework/security/oauth2/server/authorization/client/oauth2-registered-client-schema.sql")
                .build();
        // @formatter:on
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                UsernamePasswordAuthenticationToken authentication = context.getPrincipal();
                    context.getClaims().claim("roles",
                            ((UserDetails) authentication.getPrincipal()).getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            }
        };
    }
}
