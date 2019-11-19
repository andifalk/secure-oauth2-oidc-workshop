import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookService } from './book.service';
import { HttpClientModule } from "@angular/common/http";



@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ],
  exports: [
    BookService
  ]
})
export class ServicesModule { }
