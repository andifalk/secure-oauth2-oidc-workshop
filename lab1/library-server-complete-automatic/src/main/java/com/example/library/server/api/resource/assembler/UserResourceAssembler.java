package com.example.library.server.api.resource.assembler;

import com.example.library.server.api.UserRestController;
import com.example.library.server.api.resource.UserResource;
import com.example.library.server.dataaccess.User;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

  public UserResourceAssembler() {
    super(UserRestController.class, UserResource.class);
  }

  @Override
  public UserResource toResource(User user) {
    UserResource userResource = new UserResource(user);
    userResource.add(
        linkTo(methodOn(UserRestController.class).getUser(user.getIdentifier())).withSelfRel());
    return userResource;
  }
}
