package com.example.library.server.business;

import com.example.library.server.DataInitializer;
import com.example.library.server.dataaccess.BookRepository;
import com.example.library.server.dataaccess.User;
import com.example.library.server.dataaccess.UserBuilder;
import com.example.library.server.dataaccess.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceAuthorizationTest {

  private static final String EMAIL = "first.last@example.com";

  @Autowired private UserService cut;

  @SuppressWarnings("unused")
  @MockBean
  private BookRepository bookRepository;

  @SuppressWarnings("unused")
  @MockBean
  private UserRepository userRepository;

  @SuppressWarnings("unused")
  @MockBean
  private DataInitializer dataInitializer;

  @Nested
  @DisplayName("Finding a user by email")
  class FindUserByEmail {

    @WithMockUser(roles = "LIBRARY_USER")
    @Test
    @DisplayName("is authorized for LIBRARY_USER role")
    void findOneByEmailIsAuthorizedForUserRole() {
      given(userRepository.findOneByEmail(EMAIL))
          .willReturn(Optional.of(UserBuilder.user().build()));
      assertThat(cut.findOneByEmail(EMAIL)).isPresent();
    }

    @WithMockUser(roles = "LIBRARY_CURATOR")
    @Test
    @DisplayName("is authorized for LIBRARY_CURATOR role")
    void findOneByEmailIsAuthorizedForCuratorRole() {
      given(userRepository.findOneByEmail(EMAIL))
          .willReturn(Optional.of(UserBuilder.user().build()));
      assertThat(cut.findOneByEmail(EMAIL)).isPresent();
    }

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void findOneByEmailIsAuthorizedForAdminRole() {
      given(userRepository.findOneByEmail(EMAIL))
          .willReturn(Optional.of(UserBuilder.user().build()));
      assertThat(cut.findOneByEmail(EMAIL)).isPresent();
    }
  }

  @Nested
  @DisplayName("Creating a new user")
  class CreateUser {

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void createUserIsAuthorizedForAdminRole() {
      User user = UserBuilder.user().build();
      given(userRepository.save(any())).willReturn(user);
      assertThat(cut.create(user)).isNotNull();
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_CURATOR"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_CURATOR roles")
    void createUserIsForbiddenForUserAndCuratorRoles() {
      assertThatThrownBy(() -> cut.create(UserBuilder.user().build()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Updating an existing user")
  class UpdateUser {

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void updateUserIsAuthorizedForAdminRole() {
      User user = UserBuilder.user().build();
      given(userRepository.save(any())).willReturn(user);
      assertThat(cut.update(user)).isNotNull();
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_CURATOR"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_CURATOR roles")
    void updateUserIsForbiddenForUserAndCuratorRoles() {
      assertThatThrownBy(() -> cut.update(UserBuilder.user().build()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Finding a user by identifier")
  class FindUserByIdentifier {

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void findUserByIdentifierIsAuthorizedForAdminRole() {
      given(userRepository.findOneByIdentifier(any()))
          .willReturn(Optional.of(UserBuilder.user().build()));
      assertThat(cut.findByIdentifier(UUID.randomUUID())).isNotNull();
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_CURATOR"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_CURATOR roles")
    void findUserByIdentifierIsForbiddenForUserAndCuratorRoles() {
      assertThatThrownBy(() -> cut.findByIdentifier(UUID.randomUUID()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Finding all users")
  class FindAllUsers {

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void findAllUserIsAuthorizedForAdminRole() {
      given(userRepository.findAll())
          .willReturn(Collections.singletonList(UserBuilder.user().build()));
      assertThat(cut.findAll()).isNotNull().isNotEmpty();
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_CURATOR"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_CURATOR roles")
    void findAllUsersIsForbiddenForUserAndCuratorRoles() {
      assertThatThrownBy(() -> cut.findAll()).isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Deleting a user")
  class DeleteUser {

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void deleteUserIsAuthorizedForAdminRole() {
      cut.deleteByIdentifier(UUID.randomUUID());
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_CURATOR"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_CURATOR roles")
    void deleteUserIsForbiddenForUserAndCuratorRoles() {
      assertThatThrownBy(() -> cut.deleteByIdentifier(UUID.randomUUID()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
