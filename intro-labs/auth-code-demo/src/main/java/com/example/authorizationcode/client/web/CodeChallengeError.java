package com.example.authorizationcode.client.web;

public class CodeChallengeError extends RuntimeException {
  public CodeChallengeError() {
    super("PKCE: Code  challenge failed");
  }
}
