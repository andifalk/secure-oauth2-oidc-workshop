import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookService } from './book.service';



@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ],
  exports: [BookService]
})
export class ServicesModule { }
