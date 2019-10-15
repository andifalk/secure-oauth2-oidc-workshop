package com.example.library.client.credentials.batch;

import com.example.library.client.credentials.web.BookResource;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BookFieldSetMapper implements FieldSetMapper<BookResource> {

  @Override
  public BookResource mapFieldSet(FieldSet fieldSet) throws BindException {

    String authors = fieldSet.readString("authors");
    List<String> authorList = Collections.emptyList();
    if (authors != null && authors.trim().length() > 0) {
      authorList = Arrays.asList(authors.split(","));
    }
    return new BookResource(
        UUID.randomUUID(),
        fieldSet.readString("isbn"),
        fieldSet.readString("title"),
        fieldSet.readString("description"),
        authorList,
        false,
        null);
  }
}
