@echo off

setlocal

rem replace this with your own installation directory of keycloak
set KEYCLOAK_HOME=C:\Users\cp\Documents\Security\OpenIdConnect\keycloak-19.0.1\keycloak-19.0.1

rem replace username and password with your admin user

call %KEYCLOAK_HOME%\bin\kcadm.bat config credentials --server http://localhost:8080/ --realm master --user admin --password admin

call %KEYCLOAK_HOME%\bin\kcadm.bat create realms -s realm=workshop -s enabled=true

call %KEYCLOAK_HOME%\bin\kcadm.bat create partialImport -r workshop -s ifResourceExists=OVERWRITE -o -f keycloak_realm_workshop.json
