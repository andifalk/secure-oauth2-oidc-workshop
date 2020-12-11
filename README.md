[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]
![Java CI](https://github.com/andifalk/secure-oauth2-oidc-workshop/workflows/Java%20CI/badge.svg)
[![Release](https://img.shields.io/github/release/andifalk/secure-oauth2-oidc-workshop.svg?style=flat)](https://github.com/andifalk/secure-oauth2-oidc-workshop/releases)

# OAuth 2.0 / OpenID Connect Workshop

Authentication and authorization for Microservices with OAuth 2.0 (OAuth2) and OpenID Connect 1.0 (OIDC).
This contains both, theory parts on all important concepts, and hands-on practice labs.

__Table of Contents__

* [Workshop Tutorial](https://andifalk.gitbook.io/openid-connect-workshop)
* [Requirements and Setup](setup)
* [Hands-On Workshop](#hands-on-workshop)    
  * [Intro Labs](#intro-labs)
    * [Lab: Authorization Grant Flows in Action](intro-labs/oauth-grants)
    * [Demo: Auth Code Flow in Action](intro-labs/auth-code-demo)
    * [Demo: GitHub Client](intro-labs/github-client)
  * [Hands-On Labs](#hands-on-labs)
    * [Lab 1: Resource Server](lab1)
    * [Lab 2: Client (Auth Code)](lab2)
    * [Lab 3: Client (Client-Credentials)](lab3)
    * [Lab 4: Testing JWT Auth&Authz](lab4)
    * [Lab 5: JWT Testing Server](lab5)
    * [Lab 6: SPA Client (Authz Code with PKCE)](lab6)
  * [Bonus Labs](#bonus-labs)  
    * [Demo: Multi-Tenant Resource Server](bonus-labs/multi-tenant-server-app)
    * [Demo: Resource Server with Micronaut](bonus-labs/micronaut-server-app)
    * [Demo: Resource Server with Quarkus](bonus-labs/quarkus-server-app)
    * [Lab: Keycloak Testcontainers](bonus-labs/keycloak-test-containers)
* [Feedback](#feedback)
* [License](#license)    

## Workshop Tutorial

To follow the hands-on workshop please open the [workshop tutorial](https://andifalk.gitbook.io/openid-connect-workshop).

## Requirements and Setup

For the hands-on workshop you will extend a provided sample application along with guided tutorials.

The components you will build (and use) look like this:

![Architecture](docs/images/demo-architecture.png)

__Please check out the [complete documentation](application-architecture) for the sample application before 
starting with the first hands-on lab__. 

All the code currently is build using:
* [Spring Boot 2.4.x Release](https://spring.io/blog/2020/11/12/spring-boot-2-4-0-available-now) 
* [Spring Framework 5.3.x Release](https://spring.io/blog/2020/10/27/spring-framework-5-3-goes-ga)
* [Spring Security 5.4.x Release](https://spring.io/blog/2020/09/10/spring-security-5-4-goes-ga)
* [Spring Batch 4.3.x Release](https://spring.io/blog/2020/10/28/spring-batch-4-3-is-now-ga)

and is verified against the currently supported long-term version 11 of Java (The latest version 14 should work as well).

To check system requirements and setup for this workshop please follow the [setup guide](setup).

## Hands-On Workshop

### Intro Labs

* [Lab: Authorization Grant Flows in Action](intro-labs/oauth-grants)
* [Demo: Authorization Code Grant Flow in Action](intro-labs/auth-code-demo)
* [Demo: A pre-defined OAuth2 client for GitHub](intro-labs/github-client)

### Hands-On Labs

* [Lab 1: OAuth2/OIDC Resource Server](lab1)
* [Lab 2: OAuth2/OIDC Web Client (Auth Code Flow)](lab2)
* [Lab 3: OAuth2/OIDC Batch Job Client (Client-Credentials Flow)](lab3)
* [Lab 4: OAuth2/OIDC Testing Environment](lab4)
* [Lab 5: OAuth2/OIDC Angular Client](lab5)

### Bonus Labs

* [Demo: Multi-Tenant Resource Server](bonus-labs/multi-tenant-server-app)
* [Demo: OAuth2/OIDC Resource Server with Micronaut](bonus-labs/micronaut-server-app)
* [Demo: OAuth2/OIDC Resource Server with Quarkus](bonus-labs/quarkus-server-app)
* [Lab: Keycloak Testcontainers](bonus-labs/keycloak-test-containers)


## Feedback

Any feedback on this hands-on workshop is highly appreciated.

Just send an email to _andreas.falk(at)novatec-gmbh.de_ or contact me via Twitter (_@andifalk_).

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
