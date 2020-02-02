package com.example.multitenant.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class DemoRestController {

  @GetMapping
  public String index(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
    return String.format(
        "It Works (user=%s, issuer=%s)", principal.getName(), principal.getAttribute("iss"));
  }
}
