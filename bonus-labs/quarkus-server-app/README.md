# Bonus Lab: Creating an OAuth 2.0/OIDC compliant Resource Server with Quarkus

In this bonus lab we'll see how a [Quarkus](https://quarkus.io/) Microservice can be extended to an OAuth 2.0 and OpenID Connect 1.0 
compliant Resource Server.

See [Quarkus OpenID Connect Security Guide](https://quarkus.io/guides/oidc-web-app-guide) 
for all details on how to build and configure a resource server requiring JWT bearer tokens. 

## Lab Contents

* [Step 1: Generate the application](#step-1-generate-the-application)
* [Step 2: Add required extra dependencies](#step-2-add-dependencies)
* [Step 3: Configure JWT authentication and token validation](#step-3-add-oidc-configuration)
* [Step 4: Secure the endpoint](#step-4-secure-the-endpoint)
* [Step 5: Run the application](#step-5-run-and-test-basic-resource-server)

### REST API

This Quarkus demo app just provides one secured endpoint at [localhost:9096/hello](http://localhost:9096/hello).

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
Please again make sure you have set up and running
keycloak as described in [Setup Keycloak](../../setup/README.md).

<hr>

#### Step 1: Generate the application

The easiest way to create a Quarkus application is usually by using the web based init application (similar to generating a spring boot application)
by navigating your web browser to [code.quarkus.io](https://code.quarkus.io/).
As an alternative you may just use the maven based project creator instead.
This application has been generated using the [maven create command](https://docs.micronaut.io/latest/guide/index.html#buildCLI):

```bash
mvn io.quarkus:quarkus-maven-plugin:1.5.2.Final:create \
    -DprojectGroupId=com.example \
    -DprojectArtifactId=quarkus-server-app \
    -DprojectVersion=1.0.0-SNAPSHOT \
    -DclassName="com.example.ServerApp" \
    -Dextensions="resteasy-jackson" \
    -DbuildTool=gradle
```

<hr>

#### Step 2: Add dependencies  

After generation has been finished, change into the created directory.
To extend a Quarkus application into a resource server you have to make sure to add the 'quarkus-oidc' extension.  
This can be done using the following gradle command:

```bash
./gradlew addExtension --extensions="quarkus-oidc"
```

<hr>

#### Step 3: Add OIDC configuration

Quarkus requires the base URL pointing to the OIDC discovery information to fetch the public key to validate a JWT token signature. 
This is what the Quarkus configuration looks like in _application.properties_:  

```properties
quarkus.oidc.auth-server-url=http://localhost:8080/auth/realms/workshop
quarkus.oidc.client-id=library-app
```

With this configuration in place we have already a working resource server
that can handle JWt access tokens transmitted via http bearer token header. 
Quarkus also validates by default:

* the JWT signature against the queried public key(s) from _jwks_url_
* that the JWT is not expired

<hr>

#### Step 4: Secure the endpoint

Look into the class _com.example.ServerApp_ to see how Quarkus secures the only REST endpoint, and returns the details of the JWT based
principal:

```java
package com.example;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Authenticated
@Path("/hello")
public class ServerApp {
    
    @Inject
    SecurityIdentity identity;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String hello() {
        JsonWebToken jsonWebToken = (JsonWebToken) identity.getPrincipal();
        return "it works for user: " + jsonWebToken.getClaim("name") + " (" + jsonWebToken.getClaim("email") + ")";
    }
}
```

<hr>

#### Step 5: Run and test basic resource server 

Just run the quarkus application in hot-deployment development mode by using the following gradle command:

```
./gradlew quarkusDev
```

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

To make the same request to the 'hello' endpoint again (like in the beginning of this lab) we have to
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

This concludes this [Bonus Lab](./README.md).   
