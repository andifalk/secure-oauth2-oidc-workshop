# The sample workshop application

For the hands-on workshop labs you will be provided a complete spring mvc web server application together
with a corresponding spring mvc thymeleaf web client app.    

__Table of Contents__

* [Application Component View](#application-components)
* [Server Architecture](#server-architecture)
  * [REST Api](#rest-api)
  * [Server Layers](#server-layers)
  * [Users and Roles](#server-users-and-roles)
  * [Provided application](#provided-server-application)
* [Client Architecture](#client-architecture)
  * [Client Layers](#client-layers)
  * [Users and Roles](#client-users-and-roles)
  * [Provided application](#provided-client-application)

## Application Components

![Workshop Architecture](../docs/images/demo-architecture.png)

The server application provides a RESTful service for administering books and users 
(a very _lightweight_ books library).

Use cases of this application are:

* Administer books (Creating/editing/deleting books)
* List available books
* Borrow a book
* Return a borrowed book
* Administer library users 

## Server Architecture

The RESTful service for books and users is build using the Spring MVC annotation model and Spring HATEOAS.

The application also contains a complete documentation for the RESTful API that is automatically 
generated with spring rest docs. You can find this in the directory _'build/asciidoc/html5'_ after performing a full 
gradle build or online here: [REST API documentation](https://andifalk.github.io/secure-oauth2-oidc-workshop/api-doc.html).

The server application is already secured by basic authentication and also includes authorization using static roles. 

### Server Layers

The domain model of the server application is quite simple and just consists of _Book_ and _User_ models.   
The packages of the application are organized according to the different application layers:

* __api__: Contains the complete RESTful service
* __business__: The service classes (quite simple for workshop, usually these contain the business logic)
* __dataaccess__: All domain models and repositories

In addition there more packages with supporting functions:

* __common__: Classes that are reused in multiple other packages
* __config__: All spring configuration classes
* __security__: All security relevant classes, e.g. a _UserDetailsService_ implementation

### REST API

To call the provided REST API you can use curl or httpie. 
For details on how to call the REST API please consult the [REST API documentation](https://andifalk.github.io/secure-oauth2-oidc-workshop/api-doc.html) 
which also provides sample requests for curl and httpie.

### Server Users and roles

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

### Provided Server application

You can find the provided initial server application beneath the [lab 1 folder](../lab1) as 
[library-server-initial](../lab1/library-server-initial).

## Client Architecture

The client is able to fulfill most of the provided uses cases by the server application like:

* View all available books in a list
* Borrow available books
* Return my borrowed books
* Create new books

All action buttons are visible depending on user authorizations, e.g. only users with _LIBRARY_USER_ role can see
the _Borrow_ and _Return_ buttons. The _Return_

![Library Client](../docs/images/library_client.png)

### Client Layers

The domain model of the client application is quite simple and just consists of _Book_ and _User_ models.   
The packages of the application are organized according to the different application layers:

* __web__: Contains the complete spring web mvc layer with all required client side resources

In addition there is one more package with supporting functions:

* __config__: All spring configuration classes

In _resources/templates_ you find all thymeleaf html templates.  
These templates use the bootstrap framework that resides 
in _resources/static_ folder.

### Client Users and Roles

There are three target user roles for this client application:

* LIBRARY_USER: Standard library user who can list, borrow and return his currently borrowed books
* LIBRARY_CURATOR: A curator user who can add, edit or delete books
* LIBRARY_ADMIN: An administrator user who can list, add or remove users

| Username | Email                    | Password | Role            |
| ---------| ------------------------ | -------- | --------------- |
| bwayne   | bruce.wayne@example.com  | wayne    | LIBRARY_USER    |
| bbanner  | bruce.banner@example.com | banner   | LIBRARY_USER    |
| pparker  | peter.parker@example.com | parker   | LIBRARY_CURATOR |
| ckent    | clark.kent@example.com   | kent     | LIBRARY_ADMIN   |

### Provided Client application

You can find the provided initial client application beneath the [lab 2 folder](../lab2) as 
[library-client-initial](../lab2/library-client-initial).

