# Bonus Lab: Testing with Keycloak Test Containers

In this bonus lab we'll see how we can leverage [Testcontainers](https://www.testcontainers.org/) 
and [Keycloak Testcontainer](https://github.com/dasniko/testcontainers-keycloak) to create a client-side end2end test 
for our OAuth 2.0 and OpenID Connect 1.0 compliant Resource Server.

## Lab Contents

* [Step 1: Add required dependencies](#step-1-add-required-dependencies)
* [Step 2: Extend the test with testcontainers](#step-2-extend-the-end2end-integration-test-with-testcontainers)
* [Step 3: Reconfigure the JWT issuer claim](#step-3-reconfigure-the-issuer-claim-for-jwt)
* [Step 4: Run the tests](#step-4-running-the-tests)

## Learning Targets

In this lab we will add end-to-end tests for our OAuth2/OIDC compliant resource server.

We will use [Keycloak Testcontainer](https://github.com/dasniko/testcontainers-keycloak) as identity provider for this.  
So the tests will run using Keycloak as real identity provider.

In this bonus lab you will learn how to:

1. How to write automated client-side end2end tests using [RestAssured](http://rest-assured.io) 
2. How to get a real JWT from Keycloak in the tests using [Testcontainers](https://www.testcontainers.org/) and [Keycloak Testcontainer](https://github.com/dasniko/testcontainers-keycloak).

## Folder Contents

You find 2 applications in the folder _bonus-labs/keycloak-test-containers_:

* __library-server-initial__: This is the application we will use as starting point for this lab
* __library-server-complete__: This application is the completed reference for this lab 

## Start the Lab

In this lab we will implement:

* A unit test to verify the _LibraryUserJwtAuthenticationConverter_.
* An integration test to verify correct authentication & authorization for the books API using JWT

Please start this lab with the project located in _bonus-labs/keycloak-test-containers/library-server-initial_.

### Step 1: Add required dependencies

First we need to add the required dependencies

```groovy
testImplementation('com.github.dasniko:testcontainers-keycloak:1.3.1')
testImplementation('org.testcontainers:junit-jupiter:1.13.0')
```

_build.gradle_

### Step 2: Extend the End2end Integration Test with Testcontainers

Now let's start with building the test.
Open the existing test class _com.example.library.server.api.BookApiEnd2EndTest_ and add the missing parts.

In the test class you already will find two test cases for the well-known books api:

* verifyGetBooks(): This tests the happy path accessing the list of books with a valid JWT retrieved from Keycloak
* verifyGetBooksFail(): This tests the error path getting http status 401 (unauthenticated) when trying the same without a token 

First we'll add all the required parts for [Testcontainers](https://www.testcontainers.org/) and [Keycloak Testcontainer](https://github.com/dasniko/testcontainers-keycloak).

* The annotation `@Testcontainers` scans and configures all testcontainers marked with the other annotation `@Container`.
* Then we create an instance of `KeycloakContainer` that loads the same realm configuration from _keycloak_realm_workshop.json_ as used in the _real_ Keycloak instance.
* In the `setup()` operation we retrieve the required token endpoint url by retrieving the base url via `keycloak.getAuthServerUrl()`


```java
package com.example.library.server.api;

...

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

...

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@DisplayName("Verify book api")
@Tag("end2end")
class BookApiEnd2EndTest {

  @Container
  private final KeycloakContainer keycloak =
      new KeycloakContainer()
          .withRealmImportFile("keycloak_realm_workshop.json")
          .withEnv("DB_VENDOR", "h2");

  @LocalServerPort private int port;

  private String authServerUrl;

  @BeforeEach
  void setup() {
    authServerUrl = keycloak.getAuthServerUrl() + "/realms/workshop/protocol/openid-connect/token";
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
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
  ...
}
```

With this code in place you should already be able to run the tests.
You will notice that running the tests will last some time due to starting the Keycloak docker container first.
After the container started successfully the test cases will run. Here you will recognize that the test for the happy path is still failing.
This is caused by the wrong JWT issuer claim. As the Keycloak testcontainer runs at a different port than the Keycloak from previous labs also
the issuer claim has changed.

No worries, we will change this in the next section.

### Step 3: Reconfigure the Issuer Claim for JWT

Open again the same test class and change the `setup()` operation as shown here:

```java
...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
  ...
  @BeforeEach
  void setup(@Autowired NimbusJwtDecoder nimbusJwtDecoder) {
    authServerUrl = keycloak.getAuthServerUrl() + "/realms/workshop/protocol/openid-connect/token";
    nimbusJwtDecoder.setJwtValidator(
        JwtValidators.createDefaultWithIssuer(keycloak.getAuthServerUrl() + "/realms/workshop"));
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
  }
  ...
```

To change the issuer, we inject the configured _JwtDecoder_ instance, implemented by the _NimbusJwtDecoder_.
Now we can just set the issuer to the configured value of the Keycloak testcontainer.

Now the testing class should be complete and look like the following one:

```java
package com.example.library.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
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
  private final KeycloakContainer keycloak =
      new KeycloakContainer()
          .withRealmImportFile("keycloak_realm_workshop.json")
          .withEnv("DB_VENDOR", "h2");

  @LocalServerPort private int port;

  private String authServerUrl;

  @BeforeEach
  void setup(@Autowired NimbusJwtDecoder nimbusJwtDecoder) {
    authServerUrl = keycloak.getAuthServerUrl() + "/realms/workshop/protocol/openid-connect/token";
    nimbusJwtDecoder.setJwtValidator(
        JwtValidators.createDefaultWithIssuer(keycloak.getAuthServerUrl() + "/realms/workshop"));
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;
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
```

### Step 4: Running the Tests

Now you can run the tests and all tests should run fine and report a green status. 
Please also notice that the test is tagged with `@Tag("end2end")`.
This way you can for example exclude such long-running tests from the regular build and instead only run these as part of a nightly build.

Actually the gradle build excludes this test here as well using the following additional snippet in the _build.gradle_ file:

```groovy
test {
    useJUnitPlatform {
        excludeTags 'end2end'
    }
}
```
 
<hr>

This ends this bonus lab. If you like the approach with the [Testcontainers](https://www.testcontainers.org) then you may look
for other supported testcontainers like for example databases (this is also a great possibility to test against the real database 
instead of simulating this using a H2 in-memory database).
