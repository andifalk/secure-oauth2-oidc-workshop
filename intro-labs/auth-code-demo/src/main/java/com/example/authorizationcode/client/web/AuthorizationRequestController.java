package com.example.authorizationcode.client.web;

import com.example.authorizationcode.client.config.AuthCodeDemoProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/** Controller for performing OAuth2 authorization request. */
@Controller
public class AuthorizationRequestController {

  private final AuthCodeDemoProperties authCodeDemoProperties;
  private final ProofKeyForCodeExchange proofKeyForCodeExchange;

  public AuthorizationRequestController(
          AuthCodeDemoProperties authCodeDemoProperties,
          ProofKeyForCodeExchange proofKeyForCodeExchange) {
    this.authCodeDemoProperties = authCodeDemoProperties;
    this.proofKeyForCodeExchange = proofKeyForCodeExchange;
  }

  @GetMapping("/")
  public String initiateAuthRequest(Model model) throws UnsupportedEncodingException {

    model.addAttribute("pkce", authCodeDemoProperties.isPkce() ? "On" : "Off");
    model.addAttribute("authorization_endpoint", authCodeDemoProperties.getAuthorization().getEndpoint().toString());
    model.addAttribute("client_id", authCodeDemoProperties.getClientId());
    model.addAttribute("response_type", authCodeDemoProperties.getAuthorization().getResponseType());
    model.addAttribute("redirect_uri", authCodeDemoProperties.getRedirectUri().toString());
    model.addAttribute("scope", String.join(" ", authCodeDemoProperties.getAuthorization().getScope()));
    model.addAttribute("prompt", authCodeDemoProperties.getAuthorization().getPrompt() != null ?
            authCodeDemoProperties.getAuthorization().getPrompt() : "");

    String randomState = generateRandomState();
    model.addAttribute("state", randomState);

    String authorizationRequest;

    if (authCodeDemoProperties.isPkce()) {
      String codeVerifier = proofKeyForCodeExchange.createAndGetCodeVerifier();
      String codeChallenge = proofKeyForCodeExchange.createAndGetCodeChallenge(codeVerifier, ProofKeyForCodeExchange.CHALLENGE_METHOD_S_256);
      authorizationRequest = createAuthorizationRequest(randomState, codeChallenge, ProofKeyForCodeExchange.CHALLENGE_METHOD_S_256);
      model.addAttribute("code_verifier", codeVerifier);
      model.addAttribute("code_challenge", codeChallenge);
      model.addAttribute("code_challenge_method", ProofKeyForCodeExchange.CHALLENGE_METHOD_S_256);
    } else {
      authorizationRequest = createAuthorizationRequest(randomState);
      model.addAttribute("code_verifier", "n/a");
      model.addAttribute("code_challenge", "n/a");
      model.addAttribute("code_challenge_method", "n/a");
    }

    model.addAttribute("authorizationrequest", authorizationRequest);

    return "init-auth-request";
  }

  private String generateRandomState() {
    return URLEncoder.encode(RandomStringUtils.randomAlphanumeric(16), UTF_8);
  }

  private String createAuthorizationRequest(String randomState) {
    return createAuthorizationRequest(randomState, null, null);
  }

  private String createAuthorizationRequest(String randomState, String codeChallenge, String challengeMethod) {
    return
        authCodeDemoProperties.getAuthorization().getEndpoint().toString()
            + "?response_type="
            + authCodeDemoProperties.getAuthorization().getResponseType()
            + "&client_id="
            + URLEncoder.encode(authCodeDemoProperties.getClientId(), UTF_8)
            + "&state="
            + randomState
            + (codeChallenge != null ? "&code_challenge=" + codeChallenge : "")
            + (challengeMethod != null ? "&code_challenge_method=" + challengeMethod : "")
            + "&scope="
            + URLEncoder.encode(String.join(" ", authCodeDemoProperties.getAuthorization().getScope()), UTF_8)
            + "&redirect_uri="
            + URLEncoder.encode(authCodeDemoProperties.getRedirectUri().toString(), UTF_8)
            + (authCodeDemoProperties.getAuthorization().getPrompt() != null ? "&prompt="
                + authCodeDemoProperties.getAuthorization().getPrompt() : "");
  }
}
