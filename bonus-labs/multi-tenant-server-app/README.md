# Multi-Tenant Resource Server

This bonus lab demonstrates the [multi-tenancy](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#oauth2resourceserver-multitenancy) 
feature of Spring Security.

To start the resource server just run the class _com.example.multitenant.MultiTenantServerAppApplication_.

## Provided API

This resource server just provides one API at _http://localhost:9090_.
The API is secured and is only accessible by specifying a bearer JSON web token as
_Authorization_ header.

The resource server is configured as multi-tenant and as such it accepts access tokens 
by the following identity providers:

* Auth0 with Issuer _https://access-me.eu.auth0.com/_
* Okta with Issuer _https://dev-667216.oktapreview.com/oauth2/auskfyzkaoXSRnwTV0h7_

To call the API use the following commands ([Httpie](https://httpie.org/#installation) or [Curl](https://curl.haxx.se/download.html)).
You may also use [Postman](https://www.getpostman.com/downloads) instead if you like a UI more,
 
__httpie__

```
http localhost:9090 'Authorization: Bearer [access_token]'
```

__curl__

```bash
curl -H 'Authorization: Bearer [access_token]' \
-v http://localhost:9090 | jq
```

## Using OKTA

To get an access token from [Okta](https://www.okta.com/) use one of the following commands.

__httpie__

```bash
http --form https://dev-667216.oktapreview.com/oauth2/auskfyzkaoXSRnwTV0h7/v1/token grant_type=password \
username=user@example.com password=Library_access#1 client_id=0oapjlvwd21SpAWL20h7 client_secret=Fb_ig1oa9WMzzJzvm9YtFZAiYJu196ZMgy9avOb9 scope="openid profile email"
``` 

__curl__

```bash
curl -X POST -d 'grant_type=password&username=user@example.com&password=Library_access#1&client_id=0oapjlvwd21SpAWL20h7&client_secret=Fb_ig1oa9WMzzJzvm9YtFZAiYJu196ZMgy9avOb9&scope=openid%20profile%20email' \
https://dev-667216.oktapreview.com/oauth2/auskfyzkaoXSRnwTV0h7/v1/token | jq
```

## Using Auth0

To get an access token from [Auth0](https://auth0.com/) use one of the following commands.

__httpie__

```bash
http --form https://access-me.eu.auth0.com/oauth/token grant_type=password \
username=user@example.com password=user_4demo! client_id=0ed4mVHfXVs294W1Ab5K5YBb7GM7O7Tn client_secret=uP6eNUwC__v7VSQR9ggDaSfRFIAZrHBVXSyCF9pZwkrTX0LJYwjuS5uxO1Wu35Ca \
scope="openid profile email"
``` 

__curl__

```bash
curl -X POST -d 'grant_type=password&username=user@example.com&password=user_4demo!&client_id=0ed4mVHfXVs294W1Ab5K5YBb7GM7O7Tn&client_secret=uP6eNUwC__v7VSQR9ggDaSfRFIAZrHBVXSyCF9pZwkrTX0LJYwjuS5uxO1Wu35Ca&scope=openid%20profile%20email' \
https://access-me.eu.auth0.com/oauth/token | jq
```

