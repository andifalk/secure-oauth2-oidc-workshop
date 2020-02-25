package com.example.library.server.dataaccess;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  @EntityGraph(attributePaths = "roles")
  Optional<User> findOneByEmail(String email);

  Optional<User> findOneByIdentifier(UUID identifier);

  void deleteUserByIdentifier(UUID identifier);
}
