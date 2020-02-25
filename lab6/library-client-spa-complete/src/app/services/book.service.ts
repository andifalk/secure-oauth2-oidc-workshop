import { Injectable } from '@angular/core';
import { Book } from '../core/book';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from "rxjs/operators";

const BASE_URL = 'http://localhost:9091/library-server/books';

@Injectable({
  providedIn: 'root'
})
export class BookService {

  constructor(private http: HttpClient) { }

  createBook(book: Book) {
    return this.http.post(BASE_URL, book);
  }

  getBooks(): Observable<Book[]> {
    return this.http.get<any>(BASE_URL).pipe(map(response => response._embedded.bookResourceList as Book[]));
  }
}
