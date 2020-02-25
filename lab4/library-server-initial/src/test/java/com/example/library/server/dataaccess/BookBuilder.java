package com.example.library.server.dataaccess;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class BookBuilder {

  private UUID identifier = UUID.randomUUID();
  private String isbn = "978-123-456-789";
  private String title = "A book title";
  private String description = "A book description";
  private List<String> authors = new ArrayList<>();
  private boolean borrowed = false;
  private User user = null;

  private BookBuilder() {}

  public static BookBuilder book() {
    return new BookBuilder();
  }

  public BookBuilder withIdentifier(UUID identifier) {
    this.identifier = identifier;
    return this;
  }

  public BookBuilder withIsbn(String isbn) {
    this.isbn = isbn;
    return this;
  }

  public BookBuilder withTitle(String title) {
    this.title = title;
    return this;
  }

  public BookBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public BookBuilder withAuthor(String author) {
    this.authors.add(author);
    return this;
  }

  public BookBuilder borrowWith(User user) {
    this.borrowed = true;
    this.user = user;
    return this;
  }

  public Book build() {
    return new Book(identifier, isbn, title, description, authors, borrowed, user);
  }
}
