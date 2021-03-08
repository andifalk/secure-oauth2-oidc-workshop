package com.example.authorizationcode.client;

import com.example.authorizationcode.client.config.AuthCodeDemoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AuthCodeDemoProperties.class)
@SpringBootApplication
public class AuthorizationCodeDemo {

  public static void main(String[] args) {
    SpringApplication.run(AuthorizationCodeDemo.class, args);
  }
}
