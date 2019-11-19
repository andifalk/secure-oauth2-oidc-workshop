import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, CanActivate, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private oauthService: OAuthService,
    private router: Router
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    return this.oauthService
      .loadDiscoveryDocument()
      .then(_ => this.oauthService.tryLogin())
      .then((res) => {
        return this.oauthService.hasValidIdToken() && this.oauthService.hasValidAccessToken()
      });
  }

}
