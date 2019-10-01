package com.example.library.server.dataaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserBuilder {

  private UUID identifier = UUID.randomUUID();

  private String email = "first.last@example.com";

  private String password = "secret";

  private String firstName = "First";

  private String lastName = "Last";

  private List<String> roles = new ArrayList<>();

  public static UserBuilder user() {
    return new UserBuilder();
  }

  private UserBuilder() {}

  public UserBuilder withIdentifier(UUID identifier) {
    this.identifier = identifier;
    return this;
  }

  public UserBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public UserBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public UserBuilder withFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public UserBuilder withLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public UserBuilder addRole(String role) {
    this.roles.add(role);
    return this;
  }

  public User build() {
    return new User(identifier, email, password, firstName, lastName, roles);
  }
}
