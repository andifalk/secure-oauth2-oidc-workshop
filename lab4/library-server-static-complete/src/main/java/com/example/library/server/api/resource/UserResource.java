package com.example.library.server.api.resource;

import com.example.library.server.dataaccess.User;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.UUID;

public class UserResource extends RepresentationModel<UserResource> {

  private UUID identifier;

  @NotNull
  @Size(min = 1, max = 100)
  @Email
  private String email;

  @NotNull
  @Size(min = 1, max = 100)
  private String firstName;

  @NotNull
  @Size(min = 1, max = 100)
  private String lastName;

  public UserResource() {}

  public UserResource(User user) {
    this(user.getIdentifier(), user.getEmail(), user.getFirstName(), user.getLastName());
  }

  public UserResource(UUID identifier, String email, String firstName, String lastName) {
    this.identifier = identifier;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public UUID getIdentifier() {
    return identifier;
  }

  public void setIdentifier(UUID identifier) {
    this.identifier = identifier;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    UserResource that = (UserResource) o;
    return identifier.equals(that.identifier)
        && email.equals(that.email)
        && firstName.equals(that.firstName)
        && lastName.equals(that.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), identifier, email, firstName, lastName);
  }

  @Override
  public String toString() {
    return "UserResource{"
        + "identifier="
        + identifier
        + ", email='"
        + email
        + '\''
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + '}';
  }
}
