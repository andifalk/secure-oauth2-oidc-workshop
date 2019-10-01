package com.example.library.server.api;

import com.example.library.server.DataInitializer;
import com.example.library.server.api.resource.BookResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@DirtiesContext
@DisplayName("Verify book api can")
class BookApiIntegrationTests {

  @Autowired private WebApplicationContext context;

  @SuppressWarnings("unused")
  @MockBean
  private JwtDecoder jwtDecoder;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .apply(
                documentationConfiguration(restDocumentationContextProvider)
                    .operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint()))
            .build();
  }

  @Test
  @DisplayName("get list of books")
  @WithMockUser(roles = "LIBRARY_USER")
  void verifyAndDocumentGetBooks() throws Exception {

    this.mockMvc
        .perform(get("/books").header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
        .andExpect(status().isOk())
        .andDo(document("get-books"));
  }

  @Test
  @DisplayName("get single book")
  @WithMockUser(roles = "LIBRARY_USER")
  void verifyAndDocumentGetBook() throws Exception {

    this.mockMvc
        .perform(
            get("/books/{bookId}", DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER)
                .header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
        .andExpect(status().isOk())
        .andDo(document("get-book"));
  }

  @Test
  @DisplayName("delete a book")
  @WithMockUser(roles = "LIBRARY_CURATOR")
  void verifyAndDocumentDeleteBook() throws Exception {
    this.mockMvc
        .perform(
            delete("/books/{bookId}", DataInitializer.BOOK_DEVOPS_IDENTIFIER)
                .header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
        .andExpect(status().isNoContent())
        .andDo(document("delete-book"));
  }

  @Test
  @DisplayName("borrow a book")
  @WithUserDetails(
      value = "bruce.wayne@example.com",
      userDetailsServiceBeanName = "libraryUserDetailsService")
  void verifyAndDocumentBorrowBook() throws Exception {

    this.mockMvc
        .perform(
            post("/books/{bookId}/borrow", DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER)
                .header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
        .andExpect(status().isOk())
        .andDo(document("borrow-book"));
  }

  @Test
  @DisplayName("return a borrowed book")
  @WithUserDetails(
      value = "bruce.wayne@example.com",
      userDetailsServiceBeanName = "libraryUserDetailsService")
  void verifyAndDocumentReturnBook() throws Exception {

    this.mockMvc
        .perform(
            post("/books/{bookId}/return", DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER)
                .header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
        .andExpect(status().isOk())
        .andDo(document("return-book"));
  }

  @Test
  @DisplayName("create a new book")
  @WithMockUser(roles = "LIBRARY_CURATOR")
  void verifyAndDocumentCreateBook() throws Exception {

    BookResource bookResource =
        new BookResource(
            UUID.randomUUID(),
            "1234566",
            "title",
            "description",
            Collections.singletonList("Author"),
            false,
            null);

    this.mockMvc
        .perform(
            post("/books")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(bookResource))
                .header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
        .andExpect(status().isCreated())
        .andDo(document("create-book"));
  }

  @Test
  @DisplayName("update a book")
  @WithMockUser(roles = "LIBRARY_CURATOR")
  void verifyAndDocumentUpdateBook() throws Exception {

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

    this.mockMvc
        .perform(
            put("/books/{bookId}", DataInitializer.BOOK_SPRING_ACTION_IDENTIFIER)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(bookResource))
                .header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
        .andExpect(status().isOk())
        .andDo(document("update-book"));
  }
}
