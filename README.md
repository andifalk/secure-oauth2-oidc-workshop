[![License](https://img.shields.io/badge/License-Apache%20License%202.0-brightgreen.svg)][1]
![Java CI](https://github.com/andifalk/secure-oauth2-oidc-workshop/workflows/Java%20CI/badge.svg)
[![Release](https://img.shields.io/github/release/andifalk/secure-oauth2-oidc-workshop.svg?style=flat)](https://github.com/andifalk/secure-oauth2-oidc-workshop/releases)

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
    * [Lab 5: Angular Client](lab5)
  * [Bonus Labs](#bonus-labs)  
    * [Demo: Resource Server with Micronaut](bonus-labs/micronaut-server-app)

## Presentation Slides 

[Presentation Slides (PDF)](https://github.com/andifalk/secure-oauth2-oidc-workshop/raw/master/OAuth2_OpenIDConnect_Workshop_2019.pdf)

## Hands-On Workshop

For the hands-on workshop you will extend a provided sample application along with guided tutorials.

The components you will build (and use) look like this:

![Architecture](docs/images/demo-architecture.png)


__Please check out the [complete documentation](application-architecture) for the sample application before 
starting with the first hands-on lab__. 

All the code currently is build using:
* [Spring Boot 2.2.x Release](https://spring.io/blog/2019/10/16/spring-boot-2-2-0) 
* [Spring Framework 5.2.x Release](https://spring.io/blog/2019/09/30/spring-framework-5-2-goes-ga)
* [Spring Security 5.2.x Release](https://spring.io/blog/2019/10/01/spring-security-5-2-goes-ga)
* [Spring Batch 4.2.x Release](https://spring.io/blog/2019/10/02/spring-batch-4-2-in-now-ga)

and is verified against the currently supported long-term versions 8 and 11 of Java.

### Preparation and Setup

#### Requirements and useful tools

* [Java SDK](https://openjdk.java.net/install) Version 8 or 11
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

* Buildship Gradle Integration (Version 3.x). This might be already pre-installed depending 
on your eclipse variant (e.g. Eclipse JavaEE) installed
* Spring Tools 4 for Spring Boot (Spring Tool Suite 4)

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

You need a compliant OAuth 2.0 / OpenID Connect provider for this workshop.
Here we will use [Keycloak](https://keycloak.org) by RedHat/JBoss.
                  
To setup and configure Keycloak for this workshop please follow the [setup guide](setup_keycloak).

### Intro Labs

* [Demo: Authorization Code Grant Flow in Action](intro-labs/auth-code-demo)
* [Demo: A pre-defined OAuth2 client for GitHub](intro-labs/github-client)

### Hands-On Labs

* [Lab 1: OAuth2/OIDC Resource Server](lab1)
* [Lab 2: OAuth2/OIDC Web Client (Auth Code Flow)](lab2)
* [Lab 3: OAuth2/OIDC Batch Job Client (Client-Credentials Flow)](lab3)
* [Lab 4: OAuth2/OIDC Testing Environment](lab4)
* [Lab 5: OAuth2/OIDC Angular Client](lab5)

### Bonus Labs

* [Demo: OAuth2/OIDC Resource Server with Micronaut](bonus-labs/micronaut-server-app)

## Feedback

Any feedback on this hands-on workshop is highly appreciated.

Just send an email to _andreas.falk(at)novatec-gmbh.de_ or contact me via Twitter (_@andifalk_).

## License

Apache 2.0 licensed

[1]:http://www.apache.org/licenses/LICENSE-2.0.txt
