@echo off

setlocal

rem replace this with your own installation directory of keycloak
set KEYCLOAK_HOME=C:\keycloak-20.0.1

%KEYCLOAK_HOME%\bin\kc.bat start-dev --features=preview