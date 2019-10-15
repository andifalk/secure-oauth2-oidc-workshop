[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]
[![Build Status](https://travis-ci.org/andifalk/secure-oauth2-oidc-workshop.svg?branch=master)](https://travis-ci.org/andifalk/secure-oauth2-oidc-workshop)

# OAuth 2.0 / OpenID Connect 1.0 Workshop

Authentication and authorization for Microservices with OAuth 2.0 (OAuth2) and OpenID Connect 1.0 (OIDC).

[Presentation Slides (HTML5)](https://andifalk.github.io/secure-oauth2-oidc-workshop/)

# Hands-On Workshop

For the hands-on workshop you will extend a provided sample application:

An Online Book Library with following use cases:

* Administer Library Users
* Administer Books
* List available Books
* Borrow a Book
* Return a previously borrowed Book

The components you will use and build look like this:

![Architecture](docs/images/demo-architecture.png)

To use the sample library server application please consult the 
[REST API documentation](https://andifalk.github.io/secure-oauth2-oidc-workshop/api-doc.html).

All the code currently is build and tested against:
* Java 8, Java 9 and Java 11
* [Spring Boot 2.2.0 RC1](https://spring.io/blog/2019/10/03/spring-boot-2-2-0-rc1-has-been-released) 
* [Spring Framework 5.2.0 Release](https://spring.io/blog/2019/09/30/spring-framework-5-2-goes-ga)
* [Spring Security 5.2.0 Release](https://spring.io/blog/2019/10/01/spring-security-5-2-goes-ga)
* [Spring Batch 4.2.0 Release](https://spring.io/blog/2019/10/02/spring-batch-4-2-in-now-ga)

## Preparation and Setup

### Requirements and useful tools

* [Java SDK](https://openjdk.java.net/install/) Version 8, 9 or 11
* A Java IDE ([Eclipse](https://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download), [VS Code](https://code.visualstudio.com/), ...)
* [Git](https://git-scm.com/)
* [Postman](https://www.getpostman.com/downloads/), [Httpie](https://httpie.org/#installation), or [Curl](https://curl.haxx.se/download.html) for REST calls

### Get the source code
                       
Clone this GitHub repository (https://github.com/andifalk/secure-oauth2-oidc-workshop):

```
git clone https://github.com/andifalk/secure-oauth2-oidc-workshop.git oidc_workshop
```

and import whole directory into your Java IDE as __gradle project__ (look into your IDE docs on how to do this).

### Setup Keycloak
                  
1. Download 'keycloak_workshop.zip' from https://tinyurl.com/y3wjzwch (Use password: 'Workshop')
2. Extract the downloaded __keycloak_workshop.zip__ file into a new local directory of your choice 
   (this directory will be referenced as _<KEYCLOAK_INSTALL_DIR>_ in next steps)
3. To startup [Keycloak](https://keycloak.org):
    1. Open a terminal and change directory to sub directory _<KEYCLOAK_INSTALL_DIR>/bin_ and start Keycloak using 
the __standalone.sh__(Linux or Mac OS) or __standalone.bat__ (Windows) scripts
    2. Wait until keycloak has been started completely - you should see something like this `...(WildFly Core ...) started in 6902ms - Started 580 of 842 services`
    3. Now direct your browser to [localhost:8080/auth/admin](http://localhost:8080/auth/admin/)
    4. Login into the admin console using __admin/admin__ as credentials   

Now, if you see the realm _workshop_ on the left then Keycloak is ready to use it for this workshop

## Labs

* [Intro Lab: Authorization Code Grant Flow in Detail](intro-labs/auth-code-demo/README.md)
* [Intro Lab: A pre-defined OAuth2 client for GitHub](intro-labs/github-client/README.md)
* [Lab 1: OAuth2/OIDC Resource Server](lab1/README.md)
* [Lab 2: OAuth2/OIDC Web Client (Auth Code Flow)](lab2/README.md)
* [Lab 3: OAuth2/OIDC Batch Job Client (Client-Credentials Flow)](lab3/README.md)
* [Lab 4: OAuth2/OIDC Testing Environment](lab4/README.md)

## License

Apache 2.0 licensed
Copyright (c) by 2019 Andreas Falk

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
