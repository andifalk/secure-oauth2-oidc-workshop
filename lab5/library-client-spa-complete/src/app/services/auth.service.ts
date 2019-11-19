import { Injectable } from '@angular/core';
import { OAuthService, OAuthErrorEvent, AuthConfig, NullValidationHandler } from 'angular-oauth2-oidc';
import { Observable, BehaviorSubject, ReplaySubject, combineLatest } from 'rxjs';
import { map, filter } from 'rxjs/operators';
import { Router } from '@angular/router';

const authConfig: AuthConfig = {

  // Url of the Identity Provider
  issuer: 'http://localhost:8000/auth/realms/workshop',

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

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private isAuthenticatedSubject$ = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject$.asObservable();

  private isDoneLoadingSubject$ = new ReplaySubject<boolean>();
  public isDoneLoading$ = this.isDoneLoadingSubject$.asObservable();

  /**
   * Publishes `true` if and only if (a) all the asynchronous initial
   * login calls have completed or errorred, and (b) the user ended up
   * being authenticated.
   *
   * In essence, it combines:
   *
   * - the latest known state of whether the user is authorized
   * - whether the ajax calls for initial log in have all been done
   */
  public canActivateProtectedRoutes$: Observable<boolean> = combineLatest(
    this.isAuthenticated$,
    this.isDoneLoading$
  ).pipe(map(values => values.every(b => b)));

  constructor(
    private oauthService: OAuthService,
    private router: Router
  ) {
    this.oauthService.configure(authConfig);
    this.oauthService.tokenValidationHandler = new NullValidationHandler();

    this.oauthService.events
      .subscribe(_ => {
        this.isAuthenticatedSubject$.next(this.oauthService.hasValidAccessToken());
      });

    this.oauthService.setupAutomaticSilentRefresh();
  }

  public runInitialLoginSequence(): Promise<void> {
    return this.oauthService.loadDiscoveryDocument()
      .then(() => this.oauthService.tryLogin())
      .then(() => {
        this.isDoneLoadingSubject$.next(true);
        // remove query params
        this.router.navigate(['']);
      })
      .catch(() => this.isDoneLoadingSubject$.next(true));
  }
  
  public hasRole(role: string) {
    let claims: any = this.oauthService.getIdentityClaims();
    if (claims && claims.roles) {
      let roles: string[] = claims.roles;
      roles = roles.map(role => role.toUpperCase());
      return roles.includes(role.toLocaleUpperCase());
    }
    return false;
  }

  public getFullname() {
    let claims: any = this.oauthService.getIdentityClaims();
    if (claims && claims.name) {
      return claims.name;
    }
    return undefined;
  }

  public login() { this.oauthService.initCodeFlow(); }
  public logout() { this.oauthService.logOut(); }
  public refresh() { this.oauthService.silentRefresh(); }
  public hasValidToken() { return this.oauthService.hasValidAccessToken(); }


}
