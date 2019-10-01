package com.example.library.server.api.resource;

import org.springframework.hateoas.ResourceSupport;

import java.util.Collection;

public class BookListResource extends ResourceSupport {

  private final Collection<BookResource> books;

  public BookListResource(Collection<BookResource> books) {
    this.books = books;
  }

  public Collection<BookResource> getBooks() {
    return books;
  }
}
