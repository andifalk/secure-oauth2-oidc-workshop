package com.example.library.server.api.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.Collection;

public class UserListResource extends ResourceSupport {

  private final Collection<UserResource> users;

  public UserListResource(Collection<UserResource> users) {
    this.users = users;
  }

  public Collection<UserResource> getUsers() {
    return users;
  }
}
