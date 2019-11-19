import { Component, OnInit } from '@angular/core';
import { Book } from '../core/book';
import { BookService } from '../services/book.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book-create',
  templateUrl: './book-create.component.html',
  styleUrls: ['./book-create.component.css']
})
export class BookCreateComponent implements OnInit {

  bookToCreate: Book;
  author: string;

  constructor(
    private bookService: BookService,
    private router: Router
  ) {
    this.bookToCreate = new Book();
  }

  ngOnInit() {
  }

  createBook() {
    this.bookToCreate.authors = [this.author];
    this.bookService.createBook(this.bookToCreate).subscribe(
      result => {
        this.router.navigate(['/']);
      },
      error => {
        console.error(`${error}`);
      }
    );
  }

}
