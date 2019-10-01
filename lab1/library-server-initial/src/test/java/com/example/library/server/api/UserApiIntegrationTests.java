package com.example.library.server.api;

import com.example.library.server.api.resource.ModifyingUserResource;
import com.example.library.server.common.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.example.library.server.DataInitializer.CURATOR_IDENTIFIER;
import static com.example.library.server.DataInitializer.WAYNE_USER_IDENTIFIER;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@DirtiesContext
@DisplayName("Verify user api can")
@WithMockUser(roles = "LIBRARY_ADMIN")
class UserApiIntegrationTests {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .apply(
                documentationConfiguration(restDocumentationContextProvider)
                    .operationPreprocessors()
                    .withRequestDefaults(prettyPrint())
                    .withResponseDefaults(prettyPrint()))
            .build();
  }

  @Test
  @DisplayName("get list of users")
  void verifyAndDocumentGetUsers() throws Exception {

    this.mockMvc
        .perform(RestDocumentationRequestBuilders.get("/users"))
        .andExpect(status().isOk())
        .andDo(document("get-users"));
  }

  @Test
  @DisplayName("get single user")
  void verifyAndDocumentGetUser() throws Exception {

    this.mockMvc
        .perform(RestDocumentationRequestBuilders.get("/users/{userId}", WAYNE_USER_IDENTIFIER))
        .andExpect(status().isOk())
        .andDo(document("get-user"));
  }

  @Test
  @DisplayName("delete a user")
  void verifyAndDocumentDeleteUser() throws Exception {

    this.mockMvc
        .perform(RestDocumentationRequestBuilders.delete("/users/{userId}", CURATOR_IDENTIFIER))
        .andExpect(status().isNoContent())
        .andDo(document("delete-user"));
  }

  @Test
  @DisplayName("create a new user")
  void verifyAndDocumentCreateUser() throws Exception {

    ModifyingUserResource modifyingUserResource =
        new ModifyingUserResource(
            UUID.randomUUID(),
            "test@example.com",
            "test",
            "first",
            "mycoolpassword4tests",
            Collections.singletonList(Role.LIBRARY_USER));

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(modifyingUserResource)))
        .andExpect(status().isCreated())
        .andDo(document("create-user"));
  }

  @Test
  @DisplayName("update an existing user")
  void verifyAndDocumentUpdateUser() throws Exception {

    ModifyingUserResource modifyingUserResource =
        new ModifyingUserResource(
            CURATOR_IDENTIFIER,
            "curator@example.com",
            "Library",
            "Curator",
            "curator_newpassword!",
            Arrays.asList(Role.LIBRARY_USER, Role.LIBRARY_CURATOR));

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.put("/users/{userId}", CURATOR_IDENTIFIER)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(modifyingUserResource)))
        .andExpect(status().isOk())
        .andDo(document("update-user"));
  }
}
