[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]
[![Build Status](https://travis-ci.org/andifalk/secure-oauth2-oidc-workshop.svg?branch=master)](https://travis-ci.org/andifalk/secure-oauth2-oidc-workshop)

# OAuth 2.0 / OpenID Connect 1.0 Workshop

Authentication and authorization for Microservices with OAuth 2.0 (OAuth2) and OpenID Connect 1.0 (OIDC).

__Table of Contents__

* [Presentation Slides](#presentation-slides)
* [Hands-On Workshop](#hands-on-workshop)
  * [Preparation and Setup](#preparation-and-setup)
    * [System Requirements](#requirements-and-useful-tools)
    * [Get the Source Code](#get-the-source-code)
    * [Setup Keycloak](#setup-keycloak)
  * [Intro Labs](#intro-labs)
    * [Demo: Auth Code Flow in Action](intro-labs/auth-code-demo)
    * [Demo: GitHub Client](intro-labs/github-client)
  * [Hands-On Labs](#hands-on-labs)
    * [Lab 1: Resource Server](lab1)
    * [Lab 2: Client (Auth Code)](lab2)
    * [Lab 3: Client (Client-Credentials)](lab3)
    * [Lab 4: Testing](lab4)
  * [Bonus Labs](#bonus-labs)  
    * [Demo: Resource Server with Micronaut](bonus-labs/micronaut-server-app)
    * [Demo: Resource Server with Quarkus](bonus-labs/quarkus-server-app)

## Presentation Slides 

[Presentation Slides (PDF)](https://github.com/andifalk/secure-oauth2-oidc-workshop/raw/master/OAuth2_OpenIDConnect_Workshop_2019.pdf)

## Hands-On Workshop

For the hands-on workshop you will extend the following provided sample application:

An Online Book Library with following use cases:

* Administer Library Users
* Administer Books
* List available Books
* Borrow a Book
* Return a previously borrowed Book

The components you will build (and use) look like this:

![Architecture](docs/images/demo-architecture.png)

To use the RESTful API of the sample library server application please consult the 
[REST API documentation](https://andifalk.github.io/secure-oauth2-oidc-workshop/api-doc.html).

All the code currently is build using:
* [Spring Boot 2.2.0 Release](https://spring.io/blog/2019/10/16/spring-boot-2-2-0) 
* [Spring Framework 5.2.0 Release](https://spring.io/blog/2019/09/30/spring-framework-5-2-goes-ga)
* [Spring Security 5.2.0 Release](https://spring.io/blog/2019/10/01/spring-security-5-2-goes-ga)
* [Spring Batch 4.2.0 Release](https://spring.io/blog/2019/10/02/spring-batch-4-2-in-now-ga)

and is verified against Java versions 8, 10 and 11.

### Preparation and Setup

#### Requirements and useful tools

* [Java SDK](https://openjdk.java.net/install) Version 8, 10 or 11
* A Java IDE like
  * [Eclipse](https://www.eclipse.org/downloads)
  * [Spring Toolsuite](https://spring.io/tools)
  * [IntelliJ](https://www.jetbrains.com/idea/download)
  * [Visual Studio Code](https://code.visualstudio.com)
  * ...
* [Git](https://git-scm.com)
* [Postman](https://www.getpostman.com/downloads), [Httpie](https://httpie.org/#installation), or [Curl](https://curl.haxx.se/download.html) for REST calls

In case you select [Postman](https://www.getpostman.com/downloads), then the provided [Postman Collection](oidc_workshop.postman_collection.json) might be helpful.
Just import this [Postman Collection (Version 2.1 format)](oidc_workshop.postman_collection.json) into Postman.

##### IntelliJ specific requirements

IntelliJ does not require any specific additional plugins or configuration.

##### Eclipse IDE specific requirements

If you are an Eclipse user, then the usage of the Eclipse-based [Spring Toolsuite](https://spring.io/tools) is strongly recommended.
This eclipse variant already has all the required gradle and spring boot support pre-installed.

In case you want to stick to your plain Eclipse installation then you have to add the following features via the
eclipse marketplace: 

* Buildship Gradle Integration (Version 3.x)
* Spring Tools 4 - for Spring Boot

##### Visual Studio Code specific requirements

To be able to work properly in Visual Studio Code with this Spring Boot Java Gradle project you need at least these extensions:

* Java Extension Pack
* vscode-gradle-language
* VS Code Spring Boot Application Development Extension Pack

#### Get the source code
                       
Clone this GitHub repository (https://github.com/andifalk/secure-oauth2-oidc-workshop):

```
git clone https://github.com/andifalk/secure-oauth2-oidc-workshop.git oidc_workshop
```

After that you can import the whole workshop project directory into your IDE as a __gradle project__:

* [IntelliJ](https://www.jetbrains.com/idea): "New project from existing sources..." and then select 'Gradle' when prompted
* [Eclipse](https://www.eclipse.org/) or [Spring ToolSuite](https://spring.io/tools): "Import/Gradle/Existing gradle project"
* [Visual Studio Code](https://code.visualstudio.com/): Just open the root directory and wait until VS Code configured the project


#### Setup Keycloak
                  
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

### Intro Labs

* [Demo: Authorization Code Grant Flow in Action](intro-labs/auth-code-demo)
* [Demo: A pre-defined OAuth2 client for GitHub](intro-labs/github-client)

### Hands-On Labs

* [Lab 1: OAuth2/OIDC Resource Server](lab1)
* [Lab 2: OAuth2/OIDC Web Client (Auth Code Flow)](lab2)
* [Lab 3: OAuth2/OIDC Batch Job Client (Client-Credentials Flow)](lab3)
* [Lab 4: OAuth2/OIDC Testing Environment](lab4)

### Bonus Labs

* [Demo: OAuth2/OIDC Resource Server with Micronaut](bonus-labs/micronaut-server-app)
* [Demo: OAuth2/OIDC Resource Server with Quarkus](bonus-labs/quarkus-server-app)

## License

Apache 2.0 licensed
Copyright (c) by 2019 Andreas Falk

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
