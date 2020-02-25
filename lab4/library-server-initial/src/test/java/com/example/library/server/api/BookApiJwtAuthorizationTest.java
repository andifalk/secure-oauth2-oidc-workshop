package com.example.library.server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@DisplayName("Verify book api")
class BookApiJwtAuthorizationTest {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @DisplayName("can authorize to")
  @Nested
  class CanAuthorize {

    @Test
    @DisplayName("get list of books")
    void verifyGetBooks() throws Exception {}

    @Test
    @DisplayName("get single book")
    void verifyGetBook() throws Exception {}

    @Test
    @DisplayName("delete a book")
    void verifyDeleteBook() throws Exception {}

    @Test
    @DisplayName("create a new book")
    void verifyCreateBook() throws Exception {}

    @Test
    @DisplayName("update a book")
    void verifyUpdateBook() throws Exception {}
  }

  @DisplayName("cannot authorize to")
  @Nested
  class CannotAuthorize {

    @Test
    @DisplayName("get list of books")
    void verifyGetBooksUnAuthenticated() throws Exception {}

    @Test
    @DisplayName("get single book")
    void verifyGetBook() throws Exception {}

    @Test
    @DisplayName("delete a book")
    void verifyDeleteBookUnAuthorized() throws Exception {}

    @Test
    @DisplayName("delete a book with wrong role")
    void verifyDeleteBookWrongRole() throws Exception {}

    @Test
    @DisplayName("create a new book")
    void verifyCreateBookUnAuthorized() throws Exception {}

    @Test
    @DisplayName("update a book")
    void verifyUpdateBookUnAuthorized() throws Exception {}
  }
}
