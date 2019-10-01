package com.example.library.server.api.resource.assembler;

import com.example.library.server.api.BookRestController;
import com.example.library.server.api.resource.BookResource;
import com.example.library.server.dataaccess.Book;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collections;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class BookResourceAssembler extends ResourceAssemblerSupport<Book, BookResource> {

  public BookResourceAssembler() {
    super(BookRestController.class, BookResource.class);
  }

  @Override
  public BookResource toResource(Book book) {
    BookResource bookResource = new BookResource(book);
    bookResource.add(
        linkTo(methodOn(BookRestController.class).getBookById(book.getIdentifier())).withSelfRel());
    bookResource.add(
        linkTo(
                methodOn(BookRestController.class)
                    .updateBook(book.getIdentifier(), new BookResource()))
            .withRel("update"));
    bookResource.add(
        linkTo(
                methodOn(BookRestController.class)
                    .borrowBookById(
                        book.getIdentifier(),
                        new JwtAuthenticationToken(
                            new Jwt(
                                "test",
                                null,
                                null,
                                Collections.singletonMap("myheader", "header"),
                                Collections.singletonMap("test", "test")))))
            .withRel("borrow"));
    bookResource.add(
        linkTo(
                methodOn(BookRestController.class)
                    .returnBookById(
                        book.getIdentifier(),
                        new JwtAuthenticationToken(
                            new Jwt(
                                "test",
                                null,
                                null,
                                Collections.singletonMap("myheader", "header"),
                                Collections.singletonMap("test", "test")))))
            .withRel("return"));
    return bookResource;
  }
}
