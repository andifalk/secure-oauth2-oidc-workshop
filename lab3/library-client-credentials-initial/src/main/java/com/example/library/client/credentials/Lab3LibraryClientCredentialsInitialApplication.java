package com.example.library.client.credentials;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class Lab3LibraryClientCredentialsInitialApplication {

  public static void main(String[] args) {
    SpringApplication.run(Lab3LibraryClientCredentialsInitialApplication.class, args);
  }
}
