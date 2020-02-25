package com.example.library.server.api.resource;

import com.example.library.server.common.Role;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModifyingUserResource extends UserResource {

  @NotNull
  @Pattern(regexp = "[A-Za-z0-9_!]{8,100}")
  private String password;

  private List<Role> roles = new ArrayList<>();

  public ModifyingUserResource() {}

  public ModifyingUserResource(
      UUID identifier,
      String email,
      String firstName,
      String lastName,
      String password,
      List<Role> roles) {
    super(identifier, email, firstName, lastName);
    this.password = password;
    this.roles = roles;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }
}
