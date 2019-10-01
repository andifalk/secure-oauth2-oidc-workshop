package com.example.library.client.credentials;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableBatchProcessing
@SpringBootApplication
public class LibraryClientCredentialsCompleteApplication {

  public static void main(String[] args) {
    SpringApplication.run(LibraryClientCredentialsCompleteApplication.class, args);
  }
}
