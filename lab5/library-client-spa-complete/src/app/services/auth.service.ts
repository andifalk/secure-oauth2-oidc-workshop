import { Injectable } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private oauthService: OAuthService) { }

  hasRole(role: string): boolean {
    let claims: any = this.oauthService.getIdentityClaims();
    if (claims && claims.roles) {
      let roles: string[] = claims.roles;
      roles = roles.map(role => role.toUpperCase());
      return roles.includes(role.toLocaleUpperCase());
    }
    return false;
  }

  getFullname() {
    let claims: any = this.oauthService.getIdentityClaims();
    if (claims && claims.name) {
      return claims.name;
    }
    return undefined;
  }

}
