package com.example.library.server.api;

import com.example.library.server.DataInitializer;
import com.example.library.server.api.resource.BookResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@DisplayName("Verify book api")
class BookApiJwtAuthorizationTest {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
  }

  @DisplayName("can authorize to")
  @Nested
  class CanAuthorize {

    @Test
    @DisplayName("get list of books")
    void verifyGetBooks() throws Exception {

      mockMvc.perform(get("/books").with(jwt())).andExpect(status().isOk());
    }

    @Test
    @DisplayName("get single book")
    void verifyGetBook() throws Exception {

      mockMvc
          .perform(
              get("/books/{bookId}", DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER).with(jwt().jwt(jwt ->
                      jwt.tokenValue("token").header("alg", "none")
                              .claim("sub", "bwayne")
                              .claim("groups", new String[] {"library_user"}))))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("delete a book")
    void verifyDeleteBook() throws Exception {
      mockMvc
          .perform(
              delete("/books/{bookId}", DataInitializer.BOOK_DEVOPS_IDENTIFIER)
                  .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_CURATOR"))))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("create a new book")
    void verifyCreateBook() throws Exception {

      BookResource bookResource =
          new BookResource(
              UUID.randomUUID(),
              "1234566",
              "title",
              "description",
              Collections.singletonList("Author"),
              false,
              null);

      mockMvc
          .perform(
              post("/books")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(bookResource))
                  .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_CURATOR"))))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("update a book")
    void verifyUpdateBook() throws Exception {

      BookResource bookResource =
          new BookResource(
              DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER,
              "9781617291203",
              "Spring in Action: Covers Spring 5",
              "Spring in Action, Fifth Edition is a hands-on guide to the Spring Framework, "
                  + "updated for version 4. It covers the latest features, tools, and practices "
                  + "including Spring MVC, REST, Security, Web Flow, and more. You'll move between "
                  + "short snippets and an ongoing example as you learn to build simple and efficient "
                  + "J2EE applications. Author Craig Walls has a special knack for crisp and "
                  + "entertaining examples that zoom in on the features and techniques you really need.",
              Collections.singletonList("Craig Walls"),
              false,
              null);

      mockMvc
          .perform(
              put("/books/{bookId}", DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(bookResource))
                  .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_CURATOR"))))
          .andExpect(status().isOk());
    }
  }

  @DisplayName("cannot authorize to")
  @Nested
  class CannotAuthorize {

    @Test
    @DisplayName("get list of books")
    void verifyGetBooksUnAuthenticated() throws Exception {

      mockMvc.perform(get("/books")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("get single book")
    void verifyGetBook() throws Exception {

      mockMvc
              .perform(
                      get("/books/{bookId}",
                              DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER))
              .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete a book")
    void verifyDeleteBookUnAuthorized() throws Exception {
      mockMvc
              .perform(
                      delete("/books/{bookId}", DataInitializer.BOOK_DEVOPS_IDENTIFIER))
              .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("delete a book with wrong role")
    void verifyDeleteBookWrongRole() throws Exception {
      mockMvc
              .perform(
                      delete("/books/{bookId}", DataInitializer.BOOK_DEVOPS_IDENTIFIER)
                              .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LIBRARY_USER"))))
              .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("create a new book")
    void verifyCreateBookUnAuthorized() throws Exception {

      BookResource bookResource =
              new BookResource(
                      UUID.randomUUID(),
                      "1234566",
                      "title",
                      "description",
                      Collections.singletonList("Author"),
                      false,
                      null);

      mockMvc
              .perform(
                      post("/books")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(bookResource)))
              .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("update a book")
    void verifyUpdateBookUnAuthorized() throws Exception {

      BookResource bookResource =
              new BookResource(
                      DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER,
                      "9781617291203",
                      "Spring in Action: Covers Spring 5",
                      "Spring in Action, Fifth Edition is a hands-on guide to the Spring Framework, "
                              + "updated for version 4. It covers the latest features, tools, and practices "
                              + "including Spring MVC, REST, Security, Web Flow, and more. You'll move between "
                              + "short snippets and an ongoing example as you learn to build simple and efficient "
                              + "J2EE applications. Author Craig Walls has a special knack for crisp and "
                              + "entertaining examples that zoom in on the features and techniques you really need.",
                      Collections.singletonList("Craig Walls"),
                      false,
                      null);

      mockMvc
              .perform(
                      put("/books/{bookId}", DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(bookResource)))
              .andExpect(status().isUnauthorized());
    }
  }
}
