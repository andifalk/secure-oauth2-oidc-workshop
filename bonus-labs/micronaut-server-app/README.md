# Bonus Lab: Creating an OAuth 2.0/OIDC compliant Resource Server with Micronaut

In this bonus lab we'll see how a [Micronaut](https://micronaut.io/) Microservice can be extended to an OAuth 2.0 and OpenID Connect 1.0 
compliant Resource Server.

See [Micronaut JWT Security Guide](https://micronaut-projects.github.io/micronaut-security/latest/guide/#jwt) 
for all details on how to build and configure a resource server requiring JWT bearer tokens. 

## Lab Contents

* [Step 1: Generate the application](#step-1-generate-the-application)
* [Step 2: Add required extra dependencies](#step-2-add-dependencies)
* [Step 3: Configure JWT authentication and token validation](#step-3-add-jwt-configuration)
* [Step 4: Secure the endpoint](#step-4-secure-the-endpoint)
* [Step 5: Run the application](#step-5-run-and-test-basic-resource-server)

### REST API

This Micronaut demo app just provides one secured endpoint at [localhost:9096/hello](http://localhost:9096/hello).

To test if the application works as expected, either

* open Postman and configure request for [localhost:9096/hello](http://localhost:9096/hello)
* or use a command line like _curl_, _httpie_ or _postman_ (if you like a UI)

Httpie:
```bash
http localhost:9096/hello
``` 

Curl:
```bash
curl http://localhost:9096/hello
```

At this stage the application will return a 401 status.

### Users and roles

As this app uses the same Keycloak client configuration you can just use the same users as before:

| Username | Email                    | Password | Role            |
| ---------| ------------------------ | -------- | --------------- |
| bwayne   | bruce.wayne@example.com  | wayne    | LIBRARY_USER    |
| bbanner  | bruce.banner@example.com | banner   | LIBRARY_USER    |
| pparker  | peter.parker@example.com | parker   | LIBRARY_CURATOR |
| ckent    | clark.kent@example.com   | kent     | LIBRARY_ADMIN   |

We will use [Keycloak](https://keycloak.org) as identity provider.  
Please again make sure you have setup and running
keycloak as described in [Setup Keycloak](../setup_keycloak/README.md)

<hr>

#### Step 1: Generate the application

This application has been generated using the [Micronaut cli generator tool](https://docs.micronaut.io/latest/guide/index.html#buildCLI):

```bash
mn create-app micronaut-server-app
```

<hr>

#### Step 2: Add dependencies  

To extend a Micronaut application into a resource server you have to make sure the following dependencies 
are in the gradle build file _build.gradle_:

```groovy
annotationProcessor "io.micronaut:micronaut-security"
implementation "io.micronaut:micronaut-security-jwt"
```

<hr>

#### Step 3: Add jwt configuration

Micronaut requires a JWKS to validate a JWT token signature. 
This is why Micronaut requires to configure a _jwks_uri_ entry in _application.yaml_:  

```yaml
micronaut:
  security:
    enabled: true
    token:
      roles-name: 'groups'
      jwt:
        enabled: true
        signatures:
          jwks:
            keycloak:
              url: 'http://localhost:8080/auth/realms/workshop/protocol/openid-connect/certs'
```
An error you get very often with files in yaml format is that the indents are not correct. 
This can lead to unexpected errors later when you try to run all this stuff.

With this configuration in place we have already a working resource server
that can handle JWt access tokens transmitted via http bearer token header. 
Micronaut also validates by default:

* the JWT signature against the queried public key(s) from _jwks_url_
* that the JWT is not expired

In addition, Micronaut automatically maps all _'groups'_ claim entries to corresponding roles that may be checked via _@Secured_ annotations.

<hr>

#### Step 4: Secure the endpoint

Look into the class _micronaut.server.app.HelloController_ to see how the only REST endpoint is secured and the details of the JWT based
principal are read and returned:

```java
package micronaut.server.app;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.AuthenticationUserDetailsAdapter;

import java.security.Principal;
import java.util.Map;

@Secured("isAuthenticated()")
@Controller("/hello")
public class HelloController {

  @Get
  public String sayHello(Principal principal) {
    AuthenticationUserDetailsAdapter jwtClaimsSetAdapter = (AuthenticationUserDetailsAdapter) principal;
    Map<String, Object> claims = jwtClaimsSetAdapter.getAttributes();

    return "it works for user: " + claims.get("name") + " (" + claims.get("email") + ")";
  }
}
```

<hr>

#### Step 5: Run and test basic resource server 

Before starting the Micronaut application please make sure that the annotation processing is enabled for the 
java compiler in your IDE.

Now it should be possible to start the configured application _micronaut.server.app.MicronautServerApp_.

Again we use the password grant flow request to get a token for calling our new service:

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

To make the same request again to the _hello' endpoint (like in the beginning of this lab) we have to
specify the access token as part of a _Authorization_ header of type _Bearer_ like this:

httpie:

```bash
http localhost:9096/hello \
'Authorization: Bearer [access_token]'
```

curl:

```bash
curl -H 'Authorization: Bearer [access_token]' \
-v http://localhost:9096/hello | jq
```

You should now see something like this:

```bash
HTTP/1.1 200 OK
Date: Mon, 21 Oct 2019 18:24:17 GMT
connection: keep-alive
content-length: 54
content-type: application/json

it works for user: Clark Kent (clark.kent@example.com)
```

<hr>

This concludes the [Bonus Lab](./README.md).   
