package com.example.library.server.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handle(RuntimeException ex) {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    logger.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handle(Exception ex) {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    logger.error(ex.getMessage(), ex);
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<String> handle(AccessDeniedException ex) {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    logger.error(ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body("User is not authorized to use this resource");
  }
}
