package com.example.library.server.dataaccess;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {

  Optional<Book> findOneByIdentifier(UUID identifier);

  @EntityGraph(attributePaths = {"borrowedBy", "authors"})
  Optional<Book> findOneWithDetailsByIdentifier(UUID identifier);

  void deleteBookByIdentifier(UUID identifier);
}
