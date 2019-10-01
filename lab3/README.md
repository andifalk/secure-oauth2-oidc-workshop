# Lab 3: Creating an OAuth 2.0/OIDC compliant Client (Client Credentials Flow)

In this third lab we want to build again an OAuth2/OIDC client 
for the resource server we have built in Lab 1.

In contrast to [Lab 2](../lab2/README.md) this time the client will be using
the [OAuth2 client credentials grant flow](https://tools.ietf.org/html/rfc6749#section-4.4).

According to the specification this grant flow is described as follows:
<blockquote cite="https://tools.ietf.org/html/rfc6749#section-4.4">The client can request an access token using only its client credentials 
(or other supported means of authentication) when the client is requesting access to the protected resources 
under its control</blockquote>

__Important Note: The client credentials grant type MUST only be used by confidential clients.__

See [Spring Security 5 OAuth 2.0 Client reference doc](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#webclient) 
for all details on how to build and configure a reactive OAuth 2.0 client. 

## Lab Contents

* [The workshop application](#the-workshop-application)
  * [Architecture](#the-client-application)
  * [Logout Users](#logout-users)
* [The Lab 3 tutorial](#lab-3-tutorial)
  * [Lab 3 contents](#lab-3-contents)
  * [Implement the OAuth 2.0/OIDC client](#implement-the-client)
    * [Explore the initial client application](#explore-the-initial-application)
    * [Step 1: Configure as OAuth2/OIDC client w/ client credentials](#step-1-configure-as-oauth-2oidc-client-with-client-credentials)
    * [Step 2: Configure web client to send bearer access token](#step-2-configure-web-client-to-send-bearer-access-token)
    * [Step 3: Run and debug the web client authorities](#step-3-rundebug-the-oauth2-web-client-application)

## The workshop application

In this second workshop lab you will be provided a complete spring mvc web client application that works
together with the [resource server of Lab 1](../lab1/library-server-complete-custom/README.md). 

![Spring IO Workshop 2019](../docs/images/demo-architecture.png)

### The client application

The client of this lab is just able to fulfill the following uses case:

* View all available books in a list

We will use [Keycloak](https://keycloak.org) as identity provider.  
Please again make sure you have setup keycloak as described in [Setup Keycloak](../setup_keycloak/README.md).

### Logout Users

After you have logged in into the library client using keycloak your session will remain valid until
the access token has expired or the session at keycloak is invalidated.

As the library client does not have a logout functionality, you have to follow the following steps to actually log out 
users:

* Login to keycloak [admin console](http://localhost:8080/auth/admin) and navigate on the left to menu item _session_
  Here you'll see all user sessions (active/offline ones). By clicking on button _Logout all_ you can revocate 
  all active sessions.

![Spring IO Workshop 2019](../docs/images/keycloak_sessions.png)

Please note that this time we don't really log out a specific user identity. Instead there
is a configured service account at keycloak that is used for clients requesting the
client credentials grant flow.  
The service account is enabled in the client configuration section
of keycloak (menu item _Clients_ on the left). Navigate to the _library_client_, here you see
that _Service Accounts Enabled_ is set to "ON".


* After you have revocated sessions in keycloak you have to delete the current JSESSION cookie 
  for the library client. You can do this by opening the application tab in the developer tools of chrome.
  Navigate to the cookies entry on the left and select the url of the library client, then delete the cookie 
  on the right hand side 
  
![Spring IO Workshop 2019](../docs/images/devtools_cookies.png)

## Lab 3 Tutorial

Now, let's start with Lab 3. Here we will implement the required additions to get an 
OAuth2/OIDC compliant client that calls the resource server we have implemented in lab 1.

### Lab 3 Contents

In the lab 3 folder you find 2 applications:

* __library-client-credentials-initial__: This is the client application we will use as starting point for this lab
* __library-client-credentials-complete__: This client application is the completed OAuth 2.0/OIDC client for this lab 

### Implement the Client

#### Explore the initial application

First start the resource server application of Lab 1. If you could not complete the previous Lab yourself
then use and start the completed reference application 
in project [lab1/library-server-complete-custom](../lab1/library-server-complete-custom)

Then navigate your Java IDE to the __lab3/library-client-credentials-initial__ project and at first explore this project a bit.  
Then start the application by running the class _com.example.library.client.LibraryClientCredentialsInitialApplication_.

To test if the application works as expected open a web browser and 
navigate to [localhost:9092](http://localhost:9092).

You will notice that you won't be prompted for a username/password.
Instead you'll just get a message telling "Not authenticated". This is because
the web client request that is running inside this web client does not transmit
any access token to the resource server.

Now stop the client application again. You can leave the resource server running as we will need this after we have 
finished this client.

<hr>

#### Step 1: Configure as OAuth 2/OIDC client with client credentials
  
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

For configuring an OAuth2 client with the client credentials grant flow 
the only important entry is _token_endpoint_.  
For client credentials flow Spring Security 5 just requires configuration for an OAuth2 client by just specifying the _token_ uri value 
as part of the predefined spring property _spring.security.oauth2.client.provider.[id].tokenUri_.

For OAuth2 clients using client credentials as flow you have to specify the client registration (with client id, client secret, 
authorization grant type and optionally the scope). A redirect uri is not required this
time as now redirect will happen for this kind of flow.

To perform this step, open _application.yml__ and add the token uri property with the additional ones.
 
After adding this the whole configuration block beneath _spring_ 
should look like this:

```yaml
spring:
  thymeleaf:
    cache: false
  security:
    user:
      password: secret
    oauth2:
      client:
        registration:
          keycloak_client:
            client-id: 'library-client'
            client-secret: '9584640c-3804-4dcd-997b-93593cfb9ea7'
            authorizationGrantType: client_credentials
        provider:
          keycloak_client:
            tokenUri: http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token
```
An error you get very often with files in yaml format is that the indents are not correct. 
This can lead to unexpected errors later when you try to run all this stuff.

The most important entry this time is the _client_credentials_ value for the _authorizationGrantType_.

Next we need to enable the client again to be an OAuth2 client by adding 
one line _.oauth2Client()_ to the 
class _com.example.library.client.credentials.config.WebSecurityConfig_:

```java
package com.example.library.client.credentials.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
public class WebSecurityConfig {

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange()
        .anyExchange()
        .permitAll()
        .and()
        .httpBasic()
        .disable()
        .formLogin()
        .disable()
        .oauth2Client();
    return http.build();
  }
}
```

As you may notice we are using a complete reactive Spring WebFlux application.

Now you could start the application but it would still not be able to access the list of libraries
because of not transmitting the access token.

This will be done in next step.

<hr>

#### Step 2: Configure web client to send bearer access token

For all requests to the resource server we use the reactive web client, that was introduced by Spring 5.
The next required step is to make this web client aware of transmitting the required bearer access tokens
in the _Authorization_ header.

To achieve this open the class _com.example.library.client.credentials.web.BooksController_ 
and change parts of it as follows:

```java
package com.example.library.client.config;
...
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
...

@Controller
public class BooksController {

...

@GetMapping("/")
  Mono<String> index(
      @RegisteredOAuth2AuthorizedClient("keycloak_client") OAuth2AuthorizedClient authorizedClient,
      Model model) {

    return webClient
        .get()
        .uri(libraryServer + "/books")
        .headers(h -> h.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
    ...
  }
}
```
With this additions we add the bearer token header to the web client that automatically adds the
access token to all requests and _OAuth2AuthorizedClient_ initiates the client credentials grant flow if no valid 
access token is available.

<hr>

#### Step 3: Run/debug the OAuth2 web client application
  
Now re-start the library client and browse again 
to [localhost:9092](http://localhost:9092).

Now, you should be able to see the books list in the library client without requiring
an interactive login.
The authentication (i.e. retrieving an access token from the identity server) is done 
automatically behind the scenes by Spring Security 5.

If you want to see what is going on behind the scenes just add a debugging breakpoint
in the method 

_getTokenResponse(OAuth2ClientCredentialsGrantRequest authorizationGrantRequest)_ of 
class _org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient_
 
<hr>

That's a wrap for this third Lab.

If time still allows you can continue with [Lab 4](../lab4/README.md) to see how you can
configure the resource server from [Lab 1](../lab1/README.md) with a custom static private/public key pair
and create your own JWT tokens using the private key.

This is quite helpful in testing environments, e.g. doing load/performance testing and preventing
from load testing the identity server as well.
