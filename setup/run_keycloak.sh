#!/bin/sh

# replace this with your own installation directory of keycloak
export KEYCLOAK_HOME=/home/afa/Development/keycloak-12.0.4

$KEYCLOAK_HOME/bin/standalone.sh -Dkeycloak.profile=preview

