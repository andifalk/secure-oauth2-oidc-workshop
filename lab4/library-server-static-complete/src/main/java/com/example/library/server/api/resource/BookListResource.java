package com.example.library.server.api.resource;

import java.util.Collection;

public class BookListResource {

  private final Collection<BookResource> books;

  public BookListResource(Collection<BookResource> books) {
    this.books = books;
  }

  public Collection<BookResource> getBooks() {
    return books;
  }
}
