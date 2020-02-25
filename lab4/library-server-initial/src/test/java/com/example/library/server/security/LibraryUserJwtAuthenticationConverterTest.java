package com.example.library.server.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LibraryUserJwtAuthenticationConverterTest {

  @Mock private LibraryUserDetailsService libraryUserDetailsService;

  @Test
  void convertWithSuccess() {
    LibraryUserJwtAuthenticationConverter cut =
        new LibraryUserJwtAuthenticationConverter(libraryUserDetailsService);
  }

  @Test
  void convertWithFailure() {
    LibraryUserJwtAuthenticationConverter cut =
        new LibraryUserJwtAuthenticationConverter(libraryUserDetailsService);
  }
}
