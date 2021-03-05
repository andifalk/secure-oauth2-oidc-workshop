import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BookCreateComponent } from './book-create/book-create.component';
import { BookListComponent } from './book-list/book-list.component';
import { AuthGuard } from './guards/auth.guard';
import { BookCreateGuard } from './guards/book-create.guard';


const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      {path: 'createBook', component: BookCreateComponent, canActivate: [BookCreateGuard]},
      {path: '', component: BookListComponent, canActivate: [AuthGuard]}
    ]
  },
  {
    path: '**',
    component: BookListComponent,
    canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
