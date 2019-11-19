import { Component } from '@angular/core';
import { OAuthService, AuthConfig, NullValidationHandler } from 'angular-oauth2-oidc';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'library-client-spa';

  authConfig: AuthConfig = {

    // Url of the Identity Provider
    issuer: 'http://localhost:8080/auth/realms/workshop',
  
    // URL of the SPA to redirect the user to after login
    redirectUri: window.location.origin + '/index.html',
  
    // The SPA's id. The SPA is registered with this id at the auth-server
    clientId: 'spa',

    responseType: 'code',
    disableAtHashCheck: true,
  
    // set the scope for the permissions the client should request
    // The first three are defined by OIDC. The 4th is a usecase-specific one
    scope: 'openid profile'
  }

  constructor(private oauthService: OAuthService) {
    this.configure();
  }

  private configure() {
    this.oauthService.configure(this.authConfig);
    this.oauthService.tokenValidationHandler = new NullValidationHandler();
    this.oauthService.loadDiscoveryDocumentAndLogin();
  }
}
