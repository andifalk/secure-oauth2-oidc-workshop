@echo off

setlocal

rem replace this with your own installation directory of keycloak
set KEYCLOAK_HOME=C:\keycloak-12.0.4

%KEYCLOAK_HOME%\bin\standalone.bat -Dkeycloak.profile=preview
