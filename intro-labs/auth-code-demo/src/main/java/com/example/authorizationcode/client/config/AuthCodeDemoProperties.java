package com.example.authorizationcode.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * authorization:
 *     endpoint: http://localhost:8080/auth/realms/workshop/protocol/openid-connect/auth
 *     clientid: demo-client
 *     response-type: code
 *     redirect-uri: http://localhost:9095/client/callback
 *     scope: offline_access
 *     pkce: false
 *     prompt: none
 *   token:
 *     endpoint: http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token
 *     clientid: demo-client
 *     client-secret: b3ec9d3f-d1ee-4a18-b4ba-05d832c15293
 *     redirect-uri: http://localhost:9095/client/callback
 *   introspection:
 *     endpoint: http://localhost:8080/auth/realms/workshop/protocol/openid-connect/token/introspect
 */
@ConfigurationProperties(prefix = "democlient")
public class AuthCodeDemoProperties {

    @NotEmpty
    private String clientId;

    @NotNull
    private URL redirectUri;

    @NotNull
    private Boolean pkce;

    @Valid
    private Authorization authorization;

    @Valid
    private Token token;

    @Valid
    private Introspection introspection;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public URL getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(URL redirectUri) {
        this.redirectUri = redirectUri;
    }

    public boolean isPkce() {
        return pkce;
    }

    public void setPkce(boolean pkce) {
        this.pkce = pkce;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Introspection getIntrospection() {
        return introspection;
    }

    public void setIntrospection(Introspection introspection) {
        this.introspection = introspection;
    }

    @Override
    public String toString() {
        return "AuthCodeDemoProperties{" +
                "clientId='" + clientId + '\'' +
                ", redirectUri=" + redirectUri +
                ", pkce=" + pkce +
                ", authorization=" + authorization +
                ", token=" + token +
                ", introspection=" + introspection +
                '}';
    }

    public static class Authorization {
        @NotNull
        private URL endpoint;

        @NotEmpty
        private String responseType;

        @NotEmpty
        private List<String> scope = new ArrayList<>();

        private String prompt;

        public URL getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(URL endpoint) {
            this.endpoint = endpoint;
        }

        public String getResponseType() {
            return responseType;
        }

        public void setResponseType(String responseType) {
            this.responseType = responseType;
        }

        public List<String> getScope() {
            return scope;
        }

        public void setScope(List<String> scope) {
            this.scope = scope;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }

        @Override
        public String toString() {
            return "Authorization{" +
                    "endpoint='" + endpoint + '\'' +
                    ", responseType='" + responseType + '\'' +
                    ", scope=" + scope +
                    ", prompt='" + prompt + '\'' +
                    '}';
        }
    }

    public static class Token {
        @NotNull
        private URL endpoint;

        private String clientSecret;

        public URL getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(URL endpoint) {
            this.endpoint = endpoint;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "endpoint='" + endpoint + '\'' +
                    ", clientSsecret='" + clientSecret + '\'' +
                    '}';
        }
    }

    public static class Introspection {
        @NotNull
        private URL endpoint;

        public URL getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(URL endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public String toString() {
            return "Introspection{" +
                    "endpoint='" + endpoint + '\'' +
                    '}';
        }
    }

}
