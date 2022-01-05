package com.example.authorizationcode.client.web;

import com.example.authorizationcode.client.config.AuthCodeDemoProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;

@Controller
public class UserInfoController {

  private final WebClient webClient;

  private final AuthCodeDemoProperties authCodeDemoProperties;

  public UserInfoController(WebClient webClient, AuthCodeDemoProperties authCodeDemoProperties) {
    this.webClient = webClient;
    this.authCodeDemoProperties = authCodeDemoProperties;
  }

  @GetMapping("/userinfo")
  public Mono<String> userInfoRequest(@RequestParam("access_token") String accessToken, Model model)
      throws URISyntaxException {
    model.addAttribute("userinfo_endpoint", authCodeDemoProperties.getUserInfo().getEndpoint().toString());
    return performTokenIntrospectionRequest(model, accessToken);
  }

  private Mono<String> performTokenIntrospectionRequest(Model model, String accessToken)
      throws URISyntaxException {
    return webClient
        .get()
        .uri(authCodeDemoProperties.getUserInfo().getEndpoint().toURI())
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve()
        .bodyToMono(String.class)
        .map(
            s -> {
              model.addAttribute("userinfo_response", prettyJson(s));
              return "userinfo";
            })
        .log()
        .onErrorResume(
            p -> p instanceof WebClientResponseException,
            t -> {
              model.addAttribute("error", "Error getting user info");
              model.addAttribute(
                  "error_description", ((WebClientResponseException) t).getResponseBodyAsString());
              return Mono.just("error");
            });
  }

  private String prettyJson(String raw) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(raw, Object.class));
    } catch (IOException e) {
      return raw;
    }
  }
}
