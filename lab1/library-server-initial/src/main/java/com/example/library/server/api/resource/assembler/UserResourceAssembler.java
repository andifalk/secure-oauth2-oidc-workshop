package com.example.library.server.api.resource.assembler;

import com.example.library.server.api.UserRestController;
import com.example.library.server.api.resource.ModifyingUserResource;
import com.example.library.server.api.resource.UserResource;
import com.example.library.server.dataaccess.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserResourceAssembler extends RepresentationModelAssemblerSupport<User, UserResource> {

  public UserResourceAssembler() {
    super(UserRestController.class, UserResource.class);
  }

  @Override
  public UserResource toModel(User user) {
    UserResource userResource = new UserResource(user);
    userResource.add(
        linkTo(methodOn(UserRestController.class).getUser(user.getIdentifier())).withSelfRel());
    return userResource;
  }

  @Override
  public CollectionModel<UserResource> toCollectionModel(Iterable<? extends User> users) {
    CollectionModel<UserResource> userResources = super.toCollectionModel(users);
    userResources.add(
        linkTo(methodOn(UserRestController.class).getAllUsers()).withSelfRel(),
        linkTo(methodOn(UserRestController.class).createUser(new ModifyingUserResource()))
            .withRel("create"));
    return userResources;
  }
}
