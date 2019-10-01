package com.example.library.server.api;

import com.example.library.server.api.resource.ModifyingUserResource;
import com.example.library.server.api.resource.UserResource;
import com.example.library.server.business.UserService;
import com.example.library.server.dataaccess.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Validated
public class UserRestController {

  private final UserService userService;

  @Autowired
  public UserRestController(UserService userService) {
    this.userService = userService;
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public List<UserResource> getAllUsers() {
    return userService.findAll().stream()
        .map(UserResource::new)
        .collect(Collectors.toList());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResource> getUser(@PathVariable("userId") UUID userId) {
    return userService
        .findByIdentifier(userId)
        .map(u -> ResponseEntity.ok(new UserResource(u)))
        .orElse(ResponseEntity.notFound().build());
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{userId}")
  public void deleteUser(@PathVariable("userId") UUID userId) {
    userService.deleteByIdentifier(userId);
  }

  @PostMapping
  public ResponseEntity<UserResource> createUser(
      @RequestBody ModifyingUserResource modifyingUserResource) {
    User user =
        new User(
            modifyingUserResource.getIdentifier(),
            modifyingUserResource.getEmail(),
            modifyingUserResource.getFirstName(),
            modifyingUserResource.getLastName(),
            modifyingUserResource.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
    UUID identifier = userService.create(user);

    return userService
        .findByIdentifier(identifier)
        .map(
            u -> {
              URI location =
                  ServletUriComponentsBuilder.fromCurrentContextPath()
                      .path("/users/{userId}")
                      .buildAndExpand(u.getIdentifier())
                      .toUri();
              UserResource userResource = new UserResource(u);
              return ResponseEntity.created(location).body(userResource);
            })
        .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
  }

  @PutMapping("/{userId}")
  public ResponseEntity<UserResource> updateUser(
      @PathVariable("userId") UUID userIdentifier,
      @RequestBody ModifyingUserResource modifyingUserResource) {
    return userService
        .findByIdentifier(userIdentifier)
        .map(
            u -> {
              u.setEmail(modifyingUserResource.getEmail());
              u.setFirstName(modifyingUserResource.getFirstName());
              u.setLastName(modifyingUserResource.getLastName());
              u.setRoles(
                  modifyingUserResource.getRoles().stream()
                      .map(Enum::name)
                      .collect(Collectors.toList()));
              return ResponseEntity.ok(
                  new UserResource(userService.update(u)));
            })
        .orElse(ResponseEntity.notFound().build());
  }
}
