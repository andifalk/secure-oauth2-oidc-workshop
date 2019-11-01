package com.example;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Authenticated
@Path("/hello")
public class ServerApp {

    @Inject
    SecurityIdentity identity;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String hello() {
        JsonWebToken jsonWebToken = (JsonWebToken) identity.getPrincipal();
        return "it works for user: " + jsonWebToken.getClaim("name") + " (" + jsonWebToken.getClaim("email") + ")";
    }
}