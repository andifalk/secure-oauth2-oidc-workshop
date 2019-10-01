# OAuth 2.0 / OpenID Connect 1.0 Workshop

Authentication and authorization for Microservices with OAuth 2.0 and OpenID Connect 1.0 (OIDC)

# Workshop Contents

## Backend

### Resource Server Microservice

In this part we extend an existing Microservice to an OAuth 2.0 and OpenID Connect 1.0 compliant
Resource Server.

### Microservice to Microservice Calls

Here we authenticate the calls from one Microservice to another Microservice using the OAuth2/OIDC 
Client Credentials grant flow.

### API Gateway

In this step we add an API Gateway as a facade for the existing Microservices.
The API Gateway does the authentication using bearer token.

## Frontend

### Server Side Web Client

Here we extend the existing server-side web client (based on Spring MVC and Thymeleaf) to be an OAuth 2.0 and OpenID Connect 1.0 compliant client. We will use the OAuth2/OIDC authorization code grant flow for this.

### Single Page Web Client

In this step we will look into a SPA web application running inside the browser. We will extend this Angular application
to an OAuth 2.0 and OpenID Connect 1.0 compliant client application.
