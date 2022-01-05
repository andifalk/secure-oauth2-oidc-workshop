package com.example.spring.authorizationserver;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class SpringAuthorizationServerApplicationTests {

    private static final String REDIRECT_URI = "http://127.0.0.1:9095/client/callback";

    private static final String AUTHORIZATION_REQUEST = UriComponentsBuilder
            .fromPath("/oauth2/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", "demo-client")
            .queryParam("scope", "openid profile")
            .queryParam("state", "some-state")
            .queryParam("redirect_uri", REDIRECT_URI)
            .toUriString();

    @Autowired
    private WebClient webClient;

    @BeforeEach
    public void setUp() {
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getCookieManager().clearCookies();	// log out
    }

    @Test
    public void whenLoginSuccessfulThenDisplayNotFoundError() throws IOException {
        HtmlPage page = this.webClient.getPage("/");

        assertLoginPage(page);

        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        WebResponse signInResponse = signIn(page, "bwayne", "wayne").getWebResponse();
        assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());	// there is no "default" index page
    }

    @Test
    public void whenLoginFailsThenDisplayBadCredentials() throws IOException {
        HtmlPage page = this.webClient.getPage("/");

        HtmlPage loginErrorPage = signIn(page, "bwayne", "wrong-password");

        HtmlElement alert = loginErrorPage.querySelector("div[role=\"alert\"]");
        assertThat(alert).isNotNull();
        assertThat(alert.getTextContent()).isEqualTo("Bad credentials");
    }

    @Test
    public void whenNotLoggedInAndRequestingTokenThenRedirectsToLogin() throws IOException {
        HtmlPage page = this.webClient.getPage(AUTHORIZATION_REQUEST);

        assertLoginPage(page);
    }

    @Test
    public void whenLoggingInAndRequestingTokenThenRedirectsToClientApplication() throws IOException {
        // Log in
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setRedirectEnabled(false);
        signIn(this.webClient.getPage("/login"), "bwayne", "wayne");

        // Request token
        WebResponse response = this.webClient.getPage(AUTHORIZATION_REQUEST).getWebResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
        String location = response.getResponseHeaderValue("location");
        assertThat(location).startsWith(REDIRECT_URI);
        assertThat(location).contains("code=");
    }

    private static <P extends Page> P signIn(HtmlPage page, String username, String password) throws IOException {
        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        usernameInput.type(username);
        passwordInput.type(password);
        return signInButton.click();
    }

    private static void assertLoginPage(HtmlPage page) {
        assertThat(page.getUrl().toString()).endsWith("/login");

        HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
        HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
        HtmlButton signInButton = page.querySelector("button");

        assertThat(usernameInput).isNotNull();
        assertThat(passwordInput).isNotNull();
        assertThat(signInButton.getTextContent()).isEqualTo("Sign in");
    }

}
