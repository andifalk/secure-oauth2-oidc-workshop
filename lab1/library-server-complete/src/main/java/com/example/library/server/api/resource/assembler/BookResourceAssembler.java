package com.example.library.server.api.resource.assembler;

import com.example.library.server.api.BookRestController;
import com.example.library.server.api.resource.BookResource;
import com.example.library.server.dataaccess.Book;
import com.example.library.server.dataaccess.User;
import com.example.library.server.security.LibraryUser;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookResourceAssembler extends RepresentationModelAssemblerSupport<Book, BookResource> {

  public BookResourceAssembler() {
    super(BookRestController.class, BookResource.class);
  }

  @Override
  public BookResource toModel(Book book) {

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

  @Override
  public CollectionModel<BookResource> toCollectionModel(Iterable<? extends Book> entities) {
    CollectionModel<BookResource> bookResources = super.toCollectionModel(entities);
    bookResources.add(
            linkTo(methodOn(BookRestController.class).getAllBooks()).withSelfRel(),
            linkTo(methodOn(BookRestController.class).createBook(new BookResource()))
                    .withRel("create"));
    return bookResources;
  }

}
