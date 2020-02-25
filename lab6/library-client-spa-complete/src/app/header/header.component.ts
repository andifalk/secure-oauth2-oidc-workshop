import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  fullname: string;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.authService.isDoneLoading$.subscribe(_ => {
      this.fullname = this.authService.getFullname();
    });
  }

  logout() {
    this.authService.logout();
  }

}
