package com.example.library.server.api.resource.assembler;

import com.example.library.server.api.BookRestController;
import com.example.library.server.api.resource.BookListResource;
import com.example.library.server.api.resource.BookResource;
import com.example.library.server.dataaccess.Book;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class BookListResourceAssembler
    extends ResourceAssemblerSupport<Collection<Book>, BookListResource> {

  public BookListResourceAssembler() {
    super(BookRestController.class, BookListResource.class);
  }

  @Override
  public BookListResource toResource(Collection<Book> books) {

    BookListResource bookListResource =
        new BookListResource(
            books.stream()
                .map(b -> new BookResourceAssembler().toResource(b))
                .collect(Collectors.toList()));
    bookListResource.add(
        linkTo(methodOn(BookRestController.class).getAllBooks()).withSelfRel(),
        linkTo(methodOn(BookRestController.class).createBook(new BookResource()))
            .withRel("create"));
    return bookListResource;
  }
}
