package com.example.authorizationcode.client.web;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class ProofKeyForCodeExchange {

  public static final String CHALLENGE_METHOD_S_256 = "S256";
  public static final String CHALLENGE_METHOD_PLAIN = "plain";

  private static final Logger LOG = LoggerFactory.getLogger(ProofKeyForCodeExchange.class);

  private String codeVerifier;
  private String codeChallenge;
  private String challengeMethod;

  public String createAndGetCodeVerifier() {

    LOG.debug("Generating PKCE code code verifier");
    this.codeVerifier = RandomStringUtils.random(64, true, true);
    return this.codeVerifier;
  }

  public String getCodeVerifier() {
    return codeVerifier;
  }

  public String createAndGetCodeChallenge(String codeVerifier, String challengeMethod) {

    if (StringUtils.isBlank(codeVerifier)) {
      LOG.warn("Code verifier must not be empty");
      throw new CodeChallengeError();
    }

    if (codeVerifier.length() < 43 || codeVerifier.length() > 128) {
      LOG.warn("Code verifier must have a length between 43 and 128 characters");
      throw new CodeChallengeError();
    }

    if (CHALLENGE_METHOD_S_256.equalsIgnoreCase(challengeMethod)) {
      // Rehash the code verifier
      try {
        this.challengeMethod = CHALLENGE_METHOD_S_256;
        this.codeChallenge = rehashCodeVerifier(codeVerifier);
      } catch (NoSuchAlgorithmException e) {
        throw new CodeChallengeError();
      }
    } else if (challengeMethod == null || challengeMethod.isBlank() || CHALLENGE_METHOD_PLAIN.equalsIgnoreCase(challengeMethod)) {
      this.challengeMethod = CHALLENGE_METHOD_PLAIN;
      this.codeChallenge = codeVerifier;
    } else {
      LOG.warn("Invalid Code Challenge [{}]", challengeMethod);
      throw new CodeChallengeError();
    }

    return this.codeChallenge;
  }

  public String getCodeChallenge() {
    return codeChallenge;
  }

  public String getChallengeMethod() {
    return challengeMethod;
  }

  private String rehashCodeVerifier(String codeVerifier) throws NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance("SHA-256");
    final byte[] hashedBytes = digest.digest(codeVerifier.getBytes(UTF_8));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
  }
}
