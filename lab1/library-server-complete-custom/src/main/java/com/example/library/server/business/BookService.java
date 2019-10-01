package com.example.library.server.business;

import com.example.library.server.dataaccess.Book;
import com.example.library.server.dataaccess.BookRepository;
import com.example.library.server.dataaccess.User;
import com.example.library.server.dataaccess.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BookService {

  private final BookRepository bookRepository;
  private final UserRepository userRepository;
  private final IdGenerator idGenerator;

  @Autowired
  public BookService(
      BookRepository bookRepository, UserRepository userRepository, IdGenerator idGenerator) {
    this.bookRepository = bookRepository;
    this.userRepository = userRepository;
    this.idGenerator = idGenerator;
  }

  @Transactional
  @PreAuthorize("hasRole('LIBRARY_CURATOR')")
  public UUID create(Book book) {
    if (book.getIdentifier() == null) {
      book.setIdentifier(idGenerator.generateId());
    }
    return bookRepository.save(book).getIdentifier();
  }

  @Transactional
  @PreAuthorize("hasRole('LIBRARY_CURATOR')")
  public UUID update(Book book) {
    return bookRepository.save(book).getIdentifier();
  }

  @PreAuthorize("isAuthenticated()")
  public Optional<Book> findByIdentifier(UUID uuid) {
    return bookRepository.findOneByIdentifier(uuid);
  }

  @PreAuthorize("isAuthenticated()")
  public Optional<Book> findWithDetailsByIdentifier(UUID uuid) {
    return bookRepository.findOneWithDetailsByIdentifier(uuid);
  }

  @Transactional
  @PreAuthorize("hasRole('LIBRARY_USER')")
  public void borrowById(UUID bookIdentifier, UUID userIdentifier) {

    if (bookIdentifier == null || userIdentifier == null) {
      return;
    }

    userRepository
        .findOneByIdentifier(userIdentifier)
        .ifPresent(
            u ->
                bookRepository
                    .findOneByIdentifier(bookIdentifier)
                    .ifPresent(
                        b -> {
                          doBorrow(b, u);
                          bookRepository.save(b);
                        }));
  }

  @Transactional
  @PreAuthorize("hasRole('LIBRARY_USER')")
  public void returnById(UUID bookIdentifier, UUID userIdentifier) {

    if (bookIdentifier == null || userIdentifier == null) {
      return;
    }

    userRepository
        .findOneByIdentifier(userIdentifier)
        .ifPresent(
            u ->
                bookRepository
                    .findOneByIdentifier(bookIdentifier)
                    .ifPresent(
                        b -> {
                          doReturn(b, u);
                          bookRepository.save(b);
                        }));
  }

  @PreAuthorize("isAuthenticated()")
  public List<Book> findAll() {
    return bookRepository.findAll();
  }

  @Transactional
  @PreAuthorize("hasRole('LIBRARY_CURATOR')")
  public void deleteByIdentifier(UUID bookIdentifier) {
    bookRepository.deleteBookByIdentifier(bookIdentifier);
  }

  private void doReturn(Book book, User user) {
    if (book.isBorrowed()) {
      if (book.getBorrowedBy().equals(user)) {
        book.setBorrowed(false);
        book.setBorrowedBy(null);
      } else {
        throw new AccessDeniedException(
            String.format(
                "User %s cannot return a book borrowed by another user", user.getEmail()));
      }
    }
  }

  private void doBorrow(Book book, User user) {
    if (!book.isBorrowed()) {
      book.setBorrowed(true);
      book.setBorrowedBy(user);
    }
  }
}
