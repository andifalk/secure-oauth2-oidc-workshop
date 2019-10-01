package com.example.library.server.api.resource.assembler;

import com.example.library.server.api.BookRestController;
import com.example.library.server.api.resource.BookResource;
import com.example.library.server.dataaccess.Book;
import com.example.library.server.dataaccess.User;
import com.example.library.server.security.LibraryUser;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

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
                    .borrowBookById(book.getIdentifier(), new LibraryUser(new User())))
            .withRel("borrow"));
    bookResource.add(
        linkTo(
                methodOn(BookRestController.class)
                    .returnBookById(book.getIdentifier(), new LibraryUser(new User())))
            .withRel("return"));
    return bookResource;
  }
}
