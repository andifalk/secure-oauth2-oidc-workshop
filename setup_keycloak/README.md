# Preparation: Setting up Keycloak as Identity Provider

In this workshop we will use [Keycloak](https://keycloak.org) by JBoss/RedHat as local identity provider.  
[Keycloak](https://keycloak.org) is [certified for OpenID Connect 1.0](https://openid.net/developers/certified/) and 
implements OAuth 2.0 and OpenID Connect 1.0.

## Setup Keycloak

To setup Keycloak you have 2 options:

1. Run Keycloak using Docker (if you have Docker installed)
2. Local Keycloak installation & configuration

### Run Keycloak using Docker

If you have Docker installed then setting up Keycloak is quite easy.

To install, configure and run Keycloak 

1. Open a new command line terminal window
2. Change directory to subdirectory _setup_keycloak_ of the workshop repository
3. Execute script _run_keycloak_docker.sh_ or _run_keycloak_docker.bat_ (depending on your OS)

After you see the line _Started 590 of 885 services_ Keycloak is configured and running.  
Now open your web browser and navigate to [localhost:8080/auth/admin](http://localhost:8080/auth/admin) and login
using the credentials _admin_/_admin_.

### Local Installation

To setup [Keycloak](https://keycloak.org): 

1. Download the [Standard Server Distribution of Keycloak (Version 8.0.0 or higher)](https://www.keycloak.org/downloads.html).
2. Extract the downloaded zip/tar file __keycloak-x.x.x.zip__/__keycloak-x.x.x.tar-gz__ into a new local directory of your choice 
(this directory will be referenced as __<KEYCLOAK_INSTALL_DIR>__ in next steps)

#### Configure Keycloak for this Workshop

The workshop requires some configuration for Keycloak (i.e. different OAuth2/OpenID Connect clients and some user accounts).

To configure Keycloak you need to have checked out the GIT repository for this workshop.
All you need to configure Keycloak is located in the subdirectory _setup_keycloak_ of the repository.

1. Change into the subdirectory _setup_keycloak_ of the workshop git repository
2. Open the file __import_keycloak_realm.sh__ or __import_keycloak_realm.bat__ (depending on your OS) in the _setup_keycloak_ subdirectory 
   and change the value of the environment variable _KEYCLOAK_HOME_ to your __<KEYCLOAK_INSTALL_DIR>__ of step 2 and save the file
3. Now open a new command line terminal window, change into the subdirectory _setup_keycloak_ again and execute the script
   __import_keycloak_realm.sh__ or __import_keycloak_realm.bat__ (depending on your OS). 
   This starts Keycloak and imports the required configuration.
4. Wait until the import is finished (look for a line like _Started 590 of 885 services_) then 
   direct your web browser to [localhost:8080/auth](http://localhost:8080/auth/)
5. Here you have to create the initial admin user to get started. Please use _admin_ as username and also _admin_ 
   as password then click the button _Create_. Please note: In production you must use a more secure password for the admin user!
6. Now you can continue to the _Administration Console_ by clicking on the corresponding link displayed and login using the new credentials

![Keycloak Init](keycloak_initial_admin.png)

If all worked successfully you should see the settings page of the _Workshop_ realm and Keycloak is ready for this Workshop !

## Startup Keycloak

You only have to do the configuration section once.
If you have stopped Keycloak and want to start it again then follow the next lines in this section.

To startup [Keycloak](https://keycloak.org):

1. Open a terminal and change directory to sub directory __<KEYCLOAK_INSTALL_DIR>/bin__ and start Keycloak using 
the __standalone.sh__(Linux or Mac OS) or __standalone.bat__ (Windows) scripts
2. Wait until keycloak has been started completely - you should see something like this `...(WildFly Core ...) started in 6902ms - Started 580 of 842 services`
3. Now direct your browser to [localhost:8080/auth/admin](http://localhost:8080/auth/admin/)
4. Login into the admin console using __admin/admin__ as credentials

Now, if you see the realm _workshop_ on the left then Keycloak is ready to use it for this workshop

![Keycloak Workshop](keycloak_workshop.png)

## Remap default port of Keycloak

In case port _8080_ does not work on your local machine (i.e. is used by another process) then you may have to change Keycloak to use another port.
This can be done like this (e.g. for remapping port to 8090 instead of 8080):

On Linux/MAC:
```
./standalone.sh -Djboss.socket.binding.port-offset=10
```

On Windows:
```
./standalone.bat -Djboss.socket.binding.port-offset=10
```

Note: Take into account that for all URL's pointing to Keycloak in the hands-on steps you always have to use the remapped port
instead of default one (8080) as well. 

## Further Information

If you want to know more about setting up a Keycloak server for your own projects 
then please consult the [keycloak administration docs](https://www.keycloak.org/docs/latest/server_admin/index.html).