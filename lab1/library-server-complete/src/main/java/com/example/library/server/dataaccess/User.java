package com.example.library.server.dataaccess;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class User extends AbstractPersistable<Long> {

  @NotNull private UUID identifier;

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

  @ElementCollection private List<String> roles = new ArrayList<>();

  public User() {}

  public User(User user) {
    this(
        user.getIdentifier(),
        user.getEmail(),
        user.getFirstName(),
        user.getLastName(),
        user.getRoles());
  }

  @PersistenceConstructor
  public User(
      UUID identifier, String email, String firstName, String lastName, List<String> roles) {
    this.identifier = identifier;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.roles = roles;
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

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    User user = (User) o;
    return identifier.equals(user.identifier)
        && email.equals(user.email)
        && firstName.equals(user.firstName)
        && lastName.equals(user.lastName)
        && Objects.equals(roles, user.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), identifier, email, firstName, lastName, roles);
  }

  @Override
  public String toString() {
    return "User{"
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
        + ", roles="
        + roles
        + '}';
  }
}
