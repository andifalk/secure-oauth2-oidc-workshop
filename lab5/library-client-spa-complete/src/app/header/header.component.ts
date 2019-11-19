import { Component, OnInit } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  fullname: string;

  constructor(
    private oauthService: OAuthService,
    private authService: AuthService
    ) { }

  ngOnInit() {
    this.fullname = this.authService.getFullname();
  }

  logout() {
    this.oauthService.logOut();
  }

}
