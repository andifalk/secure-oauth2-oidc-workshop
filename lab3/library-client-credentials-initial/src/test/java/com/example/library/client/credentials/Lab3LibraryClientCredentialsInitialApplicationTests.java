package com.example.library.client.credentials;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("testing")
@TestPropertySource(properties = "library.server=http://localhost:8080")
@SpringBootTest
class Lab3LibraryClientCredentialsInitialApplicationTests {

  @SuppressWarnings("unused")
  @MockBean
  private ClientRegistrationRepository clientRegistrationRepository;

  @MockBean
  private WebClient webClient;

  @Test
  void contextLoads() {}
}
