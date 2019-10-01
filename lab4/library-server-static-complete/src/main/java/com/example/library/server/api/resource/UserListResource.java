package com.example.library.server.api.resource;

import java.util.Collection;

public class UserListResource {

  private final Collection<UserResource> users;

  public UserListResource(Collection<UserResource> users) {
    this.users = users;
  }

  public Collection<UserResource> getUsers() {
    return users;
  }
}
