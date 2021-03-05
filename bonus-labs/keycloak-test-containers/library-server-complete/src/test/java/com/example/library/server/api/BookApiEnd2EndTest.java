package com.example.library.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@DisplayName("Verify book api")
@Tag("end2end")
class BookApiEnd2EndTest {

  @Container
  private static final KeycloakContainer keycloak =
      new KeycloakContainer()
          .withRealmImportFile("keycloak_realm_workshop.json")
          .withEnv("DB_VENDOR", "h2");

  @LocalServerPort private int port;

  private String authServerUrl;

  @BeforeEach
  void setup() {
    this.authServerUrl = keycloak.getAuthServerUrl() + "/realms/workshop/protocol/openid-connect/token";
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }

  @SuppressWarnings("unused")
  @DynamicPropertySource
  static void jwtValidationProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
            () -> keycloak.getAuthServerUrl() + "/realms/workshop");
    registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
            () -> keycloak.getAuthServerUrl() + "/realms/workshop/protocol/openid-connect/certs");
  }

  @Test
  @DisplayName("get list of books")
  void verifyGetBooks() {

    String token = getToken();

    given()
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/library-server/books")
        .then()
        .statusCode(200);
  }

  @Test
  @DisplayName("get list of books fails without token")
  void verifyGetBooksFail() {
    when().get("/library-server/books").then().statusCode(401);
  }

  private String getToken() {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.put("grant_type", Collections.singletonList("password"));
    map.put("client_id", Collections.singletonList("library-client"));
    map.put("client_secret", Collections.singletonList("9584640c-3804-4dcd-997b-93593cfb9ea7"));
    map.put("username", Collections.singletonList("bwayne"));
    map.put("password", Collections.singletonList("wayne"));
    KeyCloakToken token =
        restTemplate.postForObject(
            authServerUrl, new HttpEntity<>(map, httpHeaders), KeyCloakToken.class);

    assert token != null;
    return token.getAccessToken();
  }

  private static class KeyCloakToken {

    private final String accessToken;

    @JsonCreator
    KeyCloakToken(@JsonProperty("access_token") final String accessToken) {
      this.accessToken = accessToken;
    }

    public String getAccessToken() {
      return accessToken;
    }
  }
}
