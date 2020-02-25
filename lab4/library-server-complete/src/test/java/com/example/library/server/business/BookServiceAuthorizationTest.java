package com.example.library.server.business;

import com.example.library.server.DataInitializer;
import com.example.library.server.dataaccess.Book;
import com.example.library.server.dataaccess.BookBuilder;
import com.example.library.server.dataaccess.BookRepository;
import com.example.library.server.dataaccess.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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
class BookServiceAuthorizationTest {

  @Autowired private BookService cut;

  @SuppressWarnings("unused")
  @MockBean
  private BookRepository bookRepository;

  @SuppressWarnings("unused")
  @MockBean
  private UserRepository userRepository;

  @SuppressWarnings("unused")
  @MockBean
  private DataInitializer dataInitializer;

  @SuppressWarnings("unused")
  @MockBean
  private OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

  @SuppressWarnings("unused")
  @MockBean
  private JwtDecoder jwtDecoder;

  @Nested
  @DisplayName("Creating a new book")
  class CreateBook {

    @WithMockUser(roles = "LIBRARY_CURATOR")
    @Test
    @DisplayName("is authorized for LIBRARY_CURATOR role")
    void createBookIsAuthorizedForRoleCurator() {
      Book book = BookBuilder.book().build();
      given(bookRepository.save(any())).willReturn(book);
      assertThat(cut.create(book)).isNotNull();
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_ADMIN"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_ADMIN roles")
    void createBookIsForbiddenForRolesUserAndAdmin() {
      assertThatThrownBy(() -> cut.create(BookBuilder.book().build()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Updating an existing book")
  class UpdateBook {

    @WithMockUser(roles = "LIBRARY_CURATOR")
    @Test
    @DisplayName("is authorized for LIBRARY_CURATOR role")
    void updateBookIsAuthorizedForCuratorRole() {
      Book book = BookBuilder.book().build();
      given(bookRepository.save(any())).willReturn(book);
      assertThat(cut.update(book)).isNotNull();
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_ADMIN"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_ADMIN roles")
    void updateBookIsForbiddenForUserAndAdminRoles() {
      assertThatThrownBy(() -> cut.update(BookBuilder.book().build()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Finding a book")
  class FindBook {
    @WithMockUser(roles = "LIBRARY_USER")
    @Test
    @DisplayName("is authorized for LIBRARY_USER role")
    void findByIdentifierIsAuthorizedForRoleUser() {
      given(bookRepository.findOneByIdentifier(any()))
          .willReturn(Optional.of(BookBuilder.book().build()));
      assertThat(cut.findByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER)).isPresent();
    }

    @WithMockUser(roles = "LIBRARY_CURATOR")
    @Test
    @DisplayName("is authorized for LIBRARY_CURATOR role")
    void findByIdentifierIsAuthorizedForRoleCurator() {
      given(bookRepository.findOneByIdentifier(any()))
          .willReturn(Optional.of(BookBuilder.book().build()));
      assertThat(cut.findByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER)).isPresent();
    }

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void findByIdentifierIsAuthorizedForRoleAdmin() {
      given(bookRepository.findOneByIdentifier(any()))
          .willReturn(Optional.of(BookBuilder.book().build()));
      assertThat(cut.findByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER)).isPresent();
    }

    @Test
    @DisplayName("is forbidden for unauthenticated users")
    void findByIdentifierIsForbiddenForUnauthenticatedUser() {
      assertThatThrownBy(() -> cut.findByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER))
          .isInstanceOf(AuthenticationException.class);
    }

    @WithMockUser(roles = "LIBRARY_USER")
    @Test
    @DisplayName("with details is authorized for LIBRARY_CURATOR role")
    void findWithDetailsByIdentifierIsAuthorizedForRoleUser() {
      given(bookRepository.findOneWithDetailsByIdentifier(any()))
          .willReturn(Optional.of(BookBuilder.book().build()));
      assertThat(cut.findWithDetailsByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER))
          .isPresent();
    }

    @WithMockUser(roles = "LIBRARY_CURATOR")
    @Test
    @DisplayName("with details is authorized for LIBRARY_CURATOR role")
    void findWithDetailsByIdentifierIsAuthorizedForRoleCurator() {
      given(bookRepository.findOneWithDetailsByIdentifier(any()))
          .willReturn(Optional.of(BookBuilder.book().build()));
      assertThat(cut.findWithDetailsByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER))
          .isPresent();
    }

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("with details is authorized for LIBRARY_ADMIN role")
    void findWithDetailsByIdentifierIsAuthorizedForRoleAdmin() {
      given(bookRepository.findOneWithDetailsByIdentifier(any()))
          .willReturn(Optional.of(BookBuilder.book().build()));
      assertThat(cut.findWithDetailsByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER))
          .isPresent();
    }

    @Test
    @DisplayName("with details is forbidden for unauthenticated users")
    void findWithDetailsByIdentifierIsForbiddenForUnauthenticatedUser() {
      assertThatThrownBy(
              () -> cut.findWithDetailsByIdentifier(DataInitializer.BOOK_CLEAN_CODE_IDENTIFIER))
          .isInstanceOf(AuthenticationException.class);
    }
  }

  @Nested
  @DisplayName("Finding all books")
  class FindAll {

    @WithMockUser(roles = "LIBRARY_USER")
    @Test
    @DisplayName("is authorized for LIBRARY_USER role")
    void findAllIsAuthorizedForUserRole() {
      given(bookRepository.findAll())
          .willReturn(Collections.singletonList(BookBuilder.book().build()));
      assertThat(cut.findAll()).isNotNull().isNotEmpty();
    }

    @WithMockUser(roles = "LIBRARY_CURATOR")
    @Test
    @DisplayName("is authorized for LIBRARY_CURATOR role")
    void findAllIsAuthorizedForCuratorRole() {
      given(bookRepository.findAll())
          .willReturn(Collections.singletonList(BookBuilder.book().build()));
      assertThat(cut.findAll()).isNotNull().isNotEmpty();
    }

    @WithMockUser(roles = "LIBRARY_ADMIN")
    @Test
    @DisplayName("is authorized for LIBRARY_ADMIN role")
    void findAllIsAuthorizedForAdminRole() {
      given(bookRepository.findAll())
          .willReturn(Collections.singletonList(BookBuilder.book().build()));
      assertThat(cut.findAll()).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("is forbidden for unauthenticated users")
    void findAllIsForbiddenForUnauthenticatedUser() {
      assertThatThrownBy(() -> cut.findAll()).isInstanceOf(AuthenticationException.class);
    }
  }

  @Nested
  @DisplayName("Borrowing a book")
  class BorrowBook {

    @WithMockUser(roles = "LIBRARY_USER")
    @Test
    @DisplayName("is authorized for LIBRARY_USER role")
    void borrowBookIsAuthorizedForUserRole() {
      cut.borrowById(UUID.randomUUID(), UUID.randomUUID());
    }

    @WithMockUser(roles = {"LIBRARY_CURATOR", "LIBRARY_ADMIN"})
    @Test
    @DisplayName("is forbidden for LIBRARY_CURATOR and LIBRARY_ADMIN roles")
    void borrowBookIsForbiddenForCuratorAndAdminRoles() {
      assertThatThrownBy(() -> cut.borrowById(UUID.randomUUID(), UUID.randomUUID()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Returning a book")
  class ReturnBook {

    @WithMockUser(roles = "LIBRARY_USER")
    @Test
    @DisplayName("is authorized for LIBRARY_USER role")
    void returnBookIsAuthorizedForUserRole() {
      cut.returnById(UUID.randomUUID(), UUID.randomUUID());
    }

    @WithMockUser(roles = {"LIBRARY_CURATOR", "LIBRARY_ADMIN"})
    @Test
    @DisplayName("is forbidden for LIBRARY_CURATOR and LIBRARY_ADMIN roles")
    void returnBookIsForbiddenForCuratorAndAdminRoles() {
      assertThatThrownBy(() -> cut.returnById(UUID.randomUUID(), UUID.randomUUID()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  @DisplayName("Deleting a book")
  class DeleteBook {

    @WithMockUser(roles = "LIBRARY_CURATOR")
    @Test
    @DisplayName("is authorized for LIBRARY_CURATOR role")
    void deleteBookIsAuthorizedForCuratorRole() {
      cut.deleteByIdentifier(UUID.randomUUID());
    }

    @WithMockUser(roles = {"LIBRARY_USER", "LIBRARY_ADMIN"})
    @Test
    @DisplayName("is forbidden for LIBRARY_USER and LIBRARY_ADMIN roles")
    void deleteBookIsForbiddenForUserAndAdminRoles() {
      assertThatThrownBy(() -> cut.deleteByIdentifier(UUID.randomUUID()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
