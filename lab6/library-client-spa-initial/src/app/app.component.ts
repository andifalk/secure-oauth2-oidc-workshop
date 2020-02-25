import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'library-client-spa';

  constructor() {
    this.configure();
  }

  private configure() {
  }
}
