import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BookCreateComponent } from './book-create/book-create.component';
import { BookListComponent } from './book-list/book-list.component';


const routes: Routes = [
  {
    path: '',
    canActivate: [],
    children: [
      {path: 'createBook', component: BookCreateComponent, canActivate: []},
      {path: '',
      component: BookListComponent,
      canActivate: []}
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
