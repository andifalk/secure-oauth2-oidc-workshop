package com.example.library.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class Lab2LibraryClientCompleteApplicationTests {

  @SuppressWarnings("unused")
  @MockBean
  private ClientRegistrationRepository clientRegistrationRepository;

  @Test
  void contextLoads() {}
}
