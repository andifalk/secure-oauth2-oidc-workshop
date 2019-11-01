package com.example.library.client.web;

public class BookListResource {

  private EmbeddedBookListResource _embedded;

  public BookListResource() {}

  public BookListResource(EmbeddedBookListResource embeddedBookListResource) {
    this._embedded = embeddedBookListResource;
  }

  public EmbeddedBookListResource get_embedded() {
    return _embedded;
  }

  public void set_embedded(EmbeddedBookListResource _embedded) {
    this._embedded = _embedded;
  }
}
