import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, CanActivate, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { filter, tap, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  private isAuthenticated: boolean;

  constructor(private authService: AuthService) {
    this.authService.isAuthenticated$.subscribe(i => this.isAuthenticated = i);
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): Observable<boolean> {
    return this.authService.isDoneLoading$
      .pipe(filter(isDone => isDone))
      .pipe(tap(_ => this.isAuthenticated || this.authService.login()))
      .pipe(map(_ => this.isAuthenticated));
  }
}
