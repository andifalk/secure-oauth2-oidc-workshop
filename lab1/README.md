# Lab 1: Creating an OAuth 2.0/OIDC compliant Resource Server

In the first lab we want to build an OAuth2/OIDC resource server.

See [Spring Security 5 Resource Server reference doc](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2resourceserver) 
for all details on how to build and configure a resource server. 

## Lab Contents

* [The workshop application](#the-workshop-application)
  * [Architecture](#architecture)
  * [REST API](#rest-api)
  * [User roles](#user-roles)
* [The Lab 1 tutorial](#lab-1-tutorial)
  * [Lab 1 contents](#lab-contents)
  * [Part 1: Implement a resource server with custom user/authorities mapping](#lab-1---part-1)
    * [Explore the initial server application](#explore-the-initial-application)    
    * [Step 1: Configure as resource server](#step-1-configure-as-resource-server)
    * [Step 2: Run and test basic resource server](#step-2-run-and-test-basic-resource-server)
    * [Step 3: Implement a custom JWT converter](#step-3-implement-a-custom-jwt-converter)
    * [Step 4: An additional JWT validator for 'audience' claim](#step-4-add-an-additional-jwt-validator-for-the-audience-claim)
  * [Part 2: Look inside a resource server with automatic scope mapping](#lab-1---part-2)
    * [Step 1: Adapting authorization checks](#step-1-adapting-the-authorization-checks)
    * [Part 1: Changing the authentication principal](#step-2-adapting-the-authentication-principal)

## The workshop application

In this first workshop lab you will be provided a complete spring mvc web server application together
with a corresponding spring mvc thymeleaf web client app (which will come into play in [lab 2](../lab2/README.md)).  
The server application is already secured by basic authentication and also includes authorization using static roles. 

![Spring IO Workshop 2019](../docs/images/demo-architecture.png)

The server application provides a RESTful service for administering books and users 
(a very _lightweight_ books library).

Use cases of this application are:

* Administer books (Creating/editing/deleting books)
* List available books
* Borrow a book
* Return a borrowed book
* Administer library users 

### Architecture

The RESTful service for books and users is build using the Spring MVC annotation model and Spring HATEOAS.

The application also contains a complete documentation for the RESTful API that is automatically 
generated with spring rest docs. You can find this in the directory build/asciidoc/html5 after performing a full 
gradle build.

The domain model of this application is quite simple and just consists of _Book_ and _User_ models.   
The packages of the application are organized as follows:

* api: Contains the complete RESTful service
* business: The service classes (quite simple for workshop, usually these contain the business logic)
* common: Classes that are reused in multiple other packages
* dataaccess: All domain models and repositories
* config: All spring configuration classes
* security: All security relevant classes, e.g. a _UserDetailsService_ implementation

### REST API

To call the provided REST API you can use curl or httpie. 
For details on how to call the REST API please consult the [REST API documentation](../docs/api-doc.html) which also provides sample requests for curl and httpie.

### Users and roles

There are three target user roles for this application:

* LIBRARY_USER: Standard library user who can list, borrow and return his currently borrowed books
* LIBRARY_CURATOR: A curator user who can add, edit or delete books
* LIBRARY_ADMIN: An administrator user who can list, add or remove users

__Important:__ We will use the following users in all subsequent labs from now on:

| Username | Email                    | Password | Role            |
| ---------| ------------------------ | -------- | --------------- |
| bwayne   | bruce.wayne@example.com  | wayne    | LIBRARY_USER    |
| bbanner  | bruce.banner@example.com | banner   | LIBRARY_USER    |
| pparker  | peter.parker@example.com | parker   | LIBRARY_CURATOR |
| ckent    | clark.kent@example.com   | kent     | LIBRARY_ADMIN   |

These users are configured for basic authentication and also later for authenticating using keycloak.

In the next labs we will build an OAuth2/OIDC resource server (lab 1) and an OAuth2/OIDC client application.
We will use [Keycloak](https://keycloak.org) as identity provider.  
Please again make sure you have setup
keycloak as described in [Setup Keycloak](../setup_keycloak/README.md)

## Lab 1 Tutorial

Lab-1 is actually split into two parts:

1. Build a resource server with __custom user & authorities mapping__ 
   and also implement additional validation of _audience_ claim in access token 
2. Having a closer look into an alternative resource server that is using __automatic authorities mapping__ 
   provided by Spring Security 5 (i.e. automatically maps scopes inside JWT tokens 
   to corresponding authorities in spring security)

### Contents of lab 1 folder

In the lab 1 folder you find 3 applications:

* __library-server-initial__: This is the application we will use as starting point for this lab
* __library-server-complete-custom__: This application is the completed one for the __first part__ of this lab 
* __library-server-complete-automatic__: This application is the completed one for the __second part__ of this lab 

### Lab 1 - Part 1

Now, let's start with part 1 of this lab. Here we will implement the required additions to get an 
OAuth2/OIDC compliant resource server with customized mapping of token claims to Spring Security authorities.

![Spring IO Workshop 2019](../docs/images/manual_role_mapping.png)

#### Explore the initial application

Please navigate your Java IDE to the __lab1/library-server-initial__ project and at first explore this project a bit.  
Then start the application by running the class _com.example.library.server.Lab1InitialLibraryServerApplication_.

To test if the application works as expected, either

* open a web browser and navigate to [localhost:9091/library-server/books](http://localhost:9091/library-server/books)
  and use 'bruce.wayne@example.com' and 'wayne' as login credentials
* or use a command line like curl or httpie or postman (if you like a UI)

Httpie:
```bash
http localhost:9091/library-server/books --auth 'bruce.wayne@example.com:wayne'
``` 

Curl:
```bash
curl http://localhost:9091/library-server/books -u bruce.wayne@example.com:wayne | jq
```

If this succeeds you should see a list of books in JSON format.  

Also try same request without specifying any user:

```bash
http localhost:9091/library-server/books
``` 

Then you should see the following response:

```http request
HTTP/1.1 401 
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
WWW-Authenticate: Basic realm="Realm"
{
    "error": "Unauthorized",
    "message": "Unauthorized",
    "path": "/library-server/books",
    "status": 401,
    "timestamp": "2019-05-09T17:26:17.571+0000"
}
``` 

Also try to request the list of users with same user credentials of 'bruce.wayne@example.com / wayne'.

Httpie:
```bash
http localhost:9091/library-server/users --auth 'bruce.wayne@example.com:wayne'
``` 

Curl:
```bash
curl http://localhost:9091/library-server/users -u bruce.wayne@example.com:wayne | jq
```

__Question:__ What response would you expect here?

<hr>

#### Step 1: Configure as resource server  
To change this application into a resource server you have to make changes in the dependencies 
of the gradle build file _build.gradle_:

Remove this dependency:
```groovy
implementation('org.springframework.boot:spring-boot-starter-security')
```
and add this dependency instead:
```groovy
implementation('org.springframework.boot:spring-boot-starter-oauth2-resource-server')
```

Spring security 5 uses the 
[OpenID Connect Discovery](https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig) specification 
to completely configure the resource server to use our keycloak instance.
  
__Make sure keycloak has been started as described in the [setup section](../setup_keycloak/README.md).__

Navigate your web browser to the url [localhost:8080/auth/realms/workshop/.well-known/openid-configuration](http://localhost:8080/auth/realms/workshop/.well-known/openid-configuration).  
Then you should see the public discovery information that keycloak provides 
(like the following that only shows partial information).

```json
{
  "issuer": "http://localhost:8080/auth/realms/workshop",
  "authorization_endpoint": "http://localhost:8080/auth/realms/workshop/protocol/openid-connect/auth",
  "token_endpoint": "http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token",
  "userinfo_endpoint": "http://localhost:8080/auth/realms/workshop/protocol/openid-connect/userinfo",
  "jwks_uri": "http://localhost:8080/auth/realms/workshop/protocol/openid-connect/certs"
}  
```

For configuring a resource server the important entries are _issuer_ and _jwks_uri_.  
Spring Security 5 automatically configures a resource server by just specifying the _issuer_ uri value 
as part of the predefined spring property _spring.security.oauth2.resourceserver.jwt.issuer-uri_ 

To perform this step, open _application.yml__ and add the issuer uri property. 
After adding this it should look like this:

```yaml
spring:
  jpa:
    open-in-view: false
  jackson:
    date-format: com.fasterxml.jackson.databind.util.StdDateFormat
    default-property-inclusion: non_null
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/auth/realms/workshop
```
An error you get very often with files in yaml format is that the indents are not correct. 
This can lead to unexpected errors later when you try to run all this stuff.

With this configuration in place we have already a working resource server
that can handle JWt access tokens transmitted via http bearer token header. 
Spring Security also validates by default:

* the JWT signature against the queried public key(s) from jwks_url
* the JWT _iss_ claim against the configured issuer uri
* that the JWT is not expired

Usually this configuration would be sufficient but as we also want to make sure that 
our resource server is working with stateless token authentication we have to configure stateless
sessions (i.e. prevent _JSESSION_ cookies).  
Starting with Spring Boot 2 you always have to configure Spring Security
yourself as soon as you introduce a class that extends _WebSecurityConfigurerAdapter_.

Open the class _com.example.library.server.config.WebSecurityConfiguration_ and change the
existing configuration like this:

```java
package com.example.library.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .fullyAuthenticated()
        .and()
        .oauth2ResourceServer()
        .jwt();
  }
  
  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
```

This configuration above...
* configures stateless sessions (i.e. no 'JSESSION' cookies an more)
* disables CSRF protection (without session cookies we do not need this any more) 
  (which also makes it possible to make post requests on the command line)
* protects any request (i.e. requires authentication)
* enables this as a resource server with expecting access tokens in JWT format

Usually _PasswordEncoder_ would not be required any more as we now do not verify passwords
any more in a resource server, but for time reasons we won't delete it. Otherwise we would need
plenty of time just removing all password related stuff from other places.

<hr>

#### Step 2: Run and test basic resource server 

Now it should be possible to re-start
the reconfigured application _com.example.library.server.Lab1InitialLibraryServerApplication_.

Now, the requests you have tried when starting this lab using basic authentication won't work any more
as we now require bearer tokens in JWT format to authenticate at our resource server.

To do this we will use the _resource owner password grant_ to directly obtain an access token
from keycloak by specifying our credentials as part of the request.  

__You may argue now: "This is just like doing basic authentication??"__

Yes, you're right. You should __ONLY__ use this grant flow for testing purposes as it
completely bypasses the base concepts of OAuth 2.

This is how this password grant request looks like:

httpie:

```bash
http --form http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token grant_type=password \
username=ckent password=kent client_id=library-client client_secret=9584640c-3804-4dcd-997b-93593cfb9ea7
``` 

curl:

```bash
curl -X POST -d 'grant_type=password&username=ckent&password=kent&client_id=library-client&client_secret=9584640c-3804-4dcd-997b-93593cfb9ea7' \
http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token
```

This should return an access token together with a refresh token:

```http request
HTTP/1.1 200 OK
Content-Type: application/json

{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgO...",
    "expires_in": 300,
    "not-before-policy": 1556650611,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIg...",
    "scope": "profile email user",
    "session_state": "c92a82d1-8e6d-44d7-a2f3-02f621066968",
    "token_type": "bearer"
}
```

To make the same request for a list of books (like in the beginning of this lab) we have to
specify the access token as part of a _Authorization_ header of type _Bearer_ like this:

httpie:

```bash
http localhost:9091/library-server/users \
'Authorization: Bearer [access_token]'
```

curl:

```bash
curl -H 'Authorization: Bearer [access_token]' \
-v http://localhost:9091/library-server/users | jq
```

You have to replace _[access_token]_ with the one you have obtained in previous request.  
Now you will get a _'403'_ response (_Forbidden_). 
This is due to the fact that Spring Security 5 automatically maps all scopes that are part of the
JWT token to the corresponding authorities.

Navigate your web browser to [jwt.io](https://jwt.io) and paste your access token into the
_Encoded_ text field. 

![Spring IO Workshop 2019](../docs/images/jwt_io.png)

If you scroll down a bit on the right hand side then you will see the following block:

```json
{
  "scope": "library_admin email profile",
  "email_verified": true,
  "name": "Clark Kent",
  "groups": [
    "library_admin"
  ],
  "preferred_username": "ckent",
  "given_name": "Clark",
  "family_name": "Kent",
  "email": "clark.kent@example.com"
}
```
As you can see our user has the scopes _library_admin_, _email_ and _profile_.
These scopes are now mapped to the Spring Security authorities 
_SCOPE_library_admin_, _SCOPE_email_ and _SCOPE_profile_.  

![Spring IO Workshop 2019](../docs/images/jwt_io_decoded.png)

If you have a look inside the _com.example.library.server.business.UserService_ class
you will notice that the corresponding method has the following authorization check:

```
@PreAuthorize("hasRole('LIBRARY_ADMIN')")
public List<User> findAll() {
  return userRepository.findAll();
}
``` 
The required authority _ROLE_LIBRARY_ADMIN_ does not match the mapped authority _SCOPE_library_admin_.
To solve this we would have to add the _SCOPE_xxx_ authorities to the existing ones like this:

```
@PreAuthorize("hasRole('LIBRARY_ADMIN') || hasAuthority('SCOPE_library_admin')")
public List<User> findAll() {
  return userRepository.findAll();
}
```  

Due to time restrictions we won't add these additional authority checks, we rather want to implement our
customized JWT to Spring Security authorities mapping. So let's continue with this next step. 

<hr>

#### Step 3: Implement a custom JWT converter 
    
To add our custom mapping for a JWT access token Spring Security requires us to implement
the interface _Converter<Jwt, AbstractAuthenticationToken>_.

In general you have two choices here:

* Map the corresponding _LibraryUser_ to the JWT token user data and read the 
  authorization data from the token and map it to Spring Security authorities
* Map the corresponding _LibraryUser_ to the JWT token user data but map locally
  stored roles of the _LibraryUser_ to Spring Security authorities.

In this workshop we will use the first approach and...
 
 * ...read the authorization data from the _groups_ claim inside the JWT token
 * ...map to our local _LibraryUser_ by reusing the _LibraryUserDetailsService_ to search
   for a user having the same email as the _email_ claim inside the JWT token

To achieve this please go ahead and create a new class _LibraryUserJwtAuthenticationConverter_
in package _com.example.library.server.security_ with the following contents:

```java
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
```
This converter maps the JWT token information to a _LibraryUser_ by associating 
these via the _email_ claim. The authorities are read from _groups_ claim in the JWT token and mapped
to the corresponding authorities.  
This way we can map these groups again to our original authorities, e.g. _ROLE_LIBRARY_ADMIN_. 

No open again the class _com.example.library.server.config.WebSecurityConfiguration_ and add this new JWT 
converter to the JWT configuration:

```java
package com.example.library.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final LibraryUserDetailsService libraryUserDetailsService;

  public WebSecurityConfiguration(LibraryUserDetailsService libraryUserDetailsService) {
    this.libraryUserDetailsService = libraryUserDetailsService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .fullyAuthenticated()
        .and()
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(libraryUserJwtAuthenticationConverter());
  }
  
  @Bean
  LibraryUserJwtAuthenticationConverter libraryUserJwtAuthenticationConverter() {
    return new LibraryUserJwtAuthenticationConverter(libraryUserDetailsService);
  }
  
  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
```

_<u>Note:</u>_: The other approach can be seen in class _LibraryUserRolesJwtAuthenticationConverter_ in completed
application in project _library-server-complete-custom_.

<hr>

#### Step 4: Add an additional JWT validator for the 'audience' claim 

Implementing an additional token validator is quite easy, you just have to implement the 
provided interface _OAuth2TokenValidator_.

According to [OpenID Connect 1.0 specification](https://openid.net/specs/openid-connect-core-1_0.html#IDToken) the _audience_ claim 
is mandatory for ID tokens:

<blockquote cite=https://openid.net/specs/openid-connect-core-1_0.html#IDToken">
Audience(s) that this ID Token is intended for. It MUST contain the OAuth 2.0 client_id of the Relying Party as an audience value. It MAY also contain identifiers for other audiences.
</blockquote>

Despite of the fact that the _audience_ claim is not specified or mandatory for access tokens
specifying and validating the _audience_ claim of access tokens is strongly recommended by OAuth 2 & OIDC experts
to avoid misusing access tokens for other resource servers.   
There is also a new [draft specification](https://tools.ietf.org/html/draft-ietf-oauth-access-token-jwt)
on the way to provide a standardized and interoperable profile as an alternative 
to the proprietary JWT access token layouts.

So we should also validate that only those requests bearing access tokens containing the 
expected value of "library-service" in the _audience_ claim are successfully authenticated.

So let's create a new class _AudienceValidator_ in package _com.example.library.server.security_
with the following contents:

```java
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
```

Adding such validator is a bit more effort as we have to replace the auto-configured JwtDecoder
with our own bean definition. An additional validator can only be added this way.

To achieve this open again the class _com.example.library.server.config.WebSecurityConfiguration_ 
one more time and add our customized JwtDecoder.

```java
package com.example.library.server.config;

import com.example.library.server.security.AudienceValidator;
import com.example.library.server.security.LibraryUserDetailsService;
import com.example.library.server.security.LibraryUserJwtAuthenticationConverter;
import com.example.library.server.security.LibraryUserRolesJwtAuthenticationConverter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

  private final LibraryUserDetailsService libraryUserDetailsService;

  public WebSecurityConfiguration(
      OAuth2ResourceServerProperties oAuth2ResourceServerProperties,
      LibraryUserDetailsService libraryUserDetailsService) {
    this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    this.libraryUserDetailsService = libraryUserDetailsService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .fullyAuthenticated()
        .and()
        .oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(libraryUserJwtAuthenticationConverter());
  }

  @Bean
  JwtDecoder jwtDecoder() {
    NimbusJwtDecoderJwkSupport jwtDecoder =
        (NimbusJwtDecoderJwkSupport)
            JwtDecoders.fromOidcIssuerLocation(
                oAuth2ResourceServerProperties.getJwt().getIssuerUri());

    OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator();
    OAuth2TokenValidator<Jwt> withIssuer =
        JwtValidators.createDefaultWithIssuer(
            oAuth2ResourceServerProperties.getJwt().getIssuerUri());
    OAuth2TokenValidator<Jwt> withAudience =
        new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

    jwtDecoder.setJwtValidator(withAudience);

    return jwtDecoder;
  }

  @Bean
  LibraryUserJwtAuthenticationConverter libraryUserJwtAuthenticationConverter() {
    return new LibraryUserJwtAuthenticationConverter(libraryUserDetailsService);
  }
  
  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }  
}
```  

Now we can re-start the application and test again the same request we had retrieved an '403' error before.

First get another fresh access token:

httpie:

```bash
http --form http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token grant_type=password \
username=ckent password=kent client_id=library-client client_secret=9584640c-3804-4dcd-997b-93593cfb9ea7
``` 

curl:

```bash
curl -X POST -d 'grant_type=password&username=ckent&password=kent&client_id=library-client&client_secret=9584640c-3804-4dcd-997b-93593cfb9ea7' \
http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token
```

This should return an access token together with a refresh token:

```http request
HTTP/1.1 200 OK
Content-Type: application/json

{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgO...",
    "expires_in": 300,
    "not-before-policy": 1556650611,
    "refresh_expires_in": 1800,
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCIg...",
    "scope": "profile email user",
    "session_state": "c92a82d1-8e6d-44d7-a2f3-02f621066968",
    "token_type": "bearer"
}
```

To make the same request for a list of users we have to
specify the access token as part of a _Authorization_ header of type _Bearer_ like this:

httpie:

```bash
http localhost:9091/library-server/users \
'Authorization: Bearer [access_token]'
```

curl:

```bash
curl -H 'Authorization: Bearer [access_token]' \
-v http://localhost:9091/library-server/users | jq
```

Now, with our previous changes this request should succeed with an '200' OK status and return a list of users.

<hr>

This ends part 1 of this lab.  
We continue with [part 2](#lab-1---part-2) to have a closer look
into a resource server just using the automatic mapping provided by Spring Security 5.

__<u>Important Note</u>__: If you could not manage to finish part 1 then just use the 
project __lab1/library-server-complete-custom__ for the next labs.

<hr>

### Lab 1 - Part 2

In part 2 of this lab we just have a look inside the completed resource server using
the automatic mapping approach provided by Spring Security 5.

__Due to time restrictions we don't implement this variant of resource server as part of this workshop!__

This serves as a reference for you to see what has to be changed when using the automatic mapping of scopes
to authorities. 
 
![Spring IO Workshop 2019](../docs/images/automatic_role_mapping.png)

To have a look open the project __lab1/library-server-complete-automatic__. 

<hr>

#### Step 1: Adapting the authorization checks 

As already mentioned in part 1 of this lab the authorities do not map to the verified ones any more
when using the automatic scope mapping:

The required authority _ROLE_LIBRARY_ADMIN_ does not match the mapped authority _SCOPE_library_admin_.
To solve this we would have to add the _SCOPE_xxx_ authorities to the existing ones like this:

```
@PreAuthorize("hasRole('LIBRARY_ADMIN') || hasAuthority('SCOPE_library_admin')")
public List<User> findAll() {
  return userRepository.findAll();
}
```  

This change would be required to perform this for all methods 
in the classes _com.example.library.server.business.BookService_ and 
_com.example.library.server.business.UserService_.

<hr>

#### Step 2: Adapting the Authentication Principal 

Please open _com.example.library.server.api.BookRestController_ class and look
for the methods to borrow or return a book:

```
@PostMapping("/{bookId}/borrow")
  public ResponseEntity<BookResource> borrowBookById(
      @PathVariable("bookId") UUID bookId, 
      @AuthenticationPrincipal LibraryUser libraryUser) {
  ...
}
```  
 
Currently the type _LibraryUser_ is expected as the authenticated principal to borrow
a book.  
Unfortunately Spring Security is not able to know how to map the JWT token information
to our desired _LibraryUser_ type automatically. Instead the JWT token is automatically mapped
to the predefined _JwtAuthenticationToken_ type.
   
To fix this we need to replace the _LibraryUser_ type with _JwtAuthenticationToken_ for
this method and manually look up the matching _LibraryUser_ in our code by using the
_LibraryUserDetailsService_:

```
@PostMapping("/{bookId}/borrow")
  public ResponseEntity<BookResource> borrowBookById(
      @PathVariable("bookId") UUID bookId,
      @AuthenticationPrincipal JwtAuthenticationToken jwtAuthenticationToken) {

    LibraryUser libraryUser =
        (LibraryUser)
            libraryUserDetailsService.loadUserByUsername(
                (String) jwtAuthenticationToken.getTokenAttributes().get("email"));

    return bookService
        .findByIdentifier(bookId)
        .map(
            b -> {
              bookService.borrowById(bookId, libraryUser.getIdentifier());
              return bookService
                  .findWithDetailsByIdentifier(b.getIdentifier())
                  .map(bb -> ResponseEntity.ok(new BookResourceAssembler().toResource(bb)))
                  .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            })
        .orElse(ResponseEntity.notFound().build());
}
```  
It is required to do the same for the _return_ books method as well: 
```
@PostMapping("/{bookId}/return")
  public ResponseEntity<BookResource> returnBookById(
      @PathVariable("bookId") UUID bookId,
      @AuthenticationPrincipal JwtAuthenticationToken jwtAuthenticationToken) {

    LibraryUser libraryUser =
        (LibraryUser)
            libraryUserDetailsService.loadUserByUsername(
                (String) jwtAuthenticationToken.getTokenAttributes().get("email"));

    return bookService
        .findByIdentifier(bookId)
        .map(
            b -> {
              bookService.returnById(bookId, libraryUser.getIdentifier());
              return bookService
                  .findWithDetailsByIdentifier(b.getIdentifier())
                  .map(bb -> ResponseEntity.ok(new BookResourceAssembler().toResource(bb)))
                  .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            })
        .orElse(ResponseEntity.notFound().build());
}
```  

<hr>

This concludes the [Lab 1](../lab1/README.md).   
We will continue with implementing the corresponding OAuth2/OIDC client for the 
resource server in project __library-server-complete-custom__.

To continue with the OAuth2/OIDC client please continue at [Lab 2](../lab2/README.md).