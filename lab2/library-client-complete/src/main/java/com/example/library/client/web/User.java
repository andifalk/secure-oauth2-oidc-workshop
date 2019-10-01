package com.example.library.client.web;

import java.util.Objects;
import java.util.UUID;

public class User {

  private UUID identifier;

  private String email;

  private String firstName;

  private String lastName;

  public User() {}

  public User(User user) {
    this(user.getIdentifier(), user.getEmail(), user.getFirstName(), user.getLastName());
  }

  public User(UUID identifier, String email, String firstName, String lastName) {
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
    User user = (User) o;
    return identifier.equals(user.identifier)
        && email.equals(user.email)
        && firstName.equals(user.firstName)
        && lastName.equals(user.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), identifier, email, firstName, lastName);
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
        + '}';
  }
}
