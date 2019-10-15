package com.example.library.client.credentials.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Profile("!testing")
@Configuration
public class WebClientConfiguration {

  @Bean
  WebClient webClient() {
    return WebClient.builder().build();
  }
}
