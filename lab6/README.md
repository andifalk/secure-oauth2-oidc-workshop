# Lab 6: Creating an OAuth 2.0/OIDC compliant Single-Page-Application using Angular

In this third lab we want to build again an OAuth2/OIDC client for the resource server 
we have built in [lab 1](../lab1).

In contrast to [Lab 2](../lab2) and [Lab 3](../lab3) this time the client will be using
the [Angular](https://angular.io/) and the corresponding [OAuth 2.0/OIDC library](https://github.com/manfredsteyer/angular-oauth2-oidc).

## Lab Contents

* [Learning Targets](#learning-targets)
* [Folder Contents](#folder-contents)
* [Hands-On: Implement the OAuth 2.0/OIDC batch client](#start-the-lab)
    * [Explore the initial client application](#explore-the-initial-application)
    * [Step 1: Configure as OAuth2/OIDC client w/ client credentials](#step-1-configure-as-oauth-2oidc-client-with-client-credentials)
    * [Step 2: Configure web client to send bearer access token](#step-2-configure-web-client-to-send-bearer-access-token)
    * [Step 3: Run and debug the web client authorities](#step-3-rundebug-the-oauth2-batch-job-client-application)

## Learning Targets

In this sixth workshop lab you will be learning how to build an OAuth 2.0/OIDC compliant frontend using Angular, 
that works together with the [resource server of Lab 1](../lab1/library-server-complete-custom/README.md). 

In contrast to [Lab 2](../lab2/README.md) this time we will see how to build a client with a browser environment without having a secure back-channel. 
We will use the most modern way to make this possible by facilitating the [authorization code](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1) grant 
with [PKCE](https://tools.ietf.org/html/rfc7636).

The latest IETF working group drafts of [OAuth 2.0 for Browser-Based Apps](https://tools.ietf.org/html/draft-ietf-oauth-browser-based-apps) 
and [OAuth 2.0 Security Best Current Practice](https://tools.ietf.org/html/draft-ietf-oauth-security-topics) clearly
deprecated the [implicit grant](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.2) in 
favor of the [authorization code](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1) + [PKCE](https://tools.ietf.org/html/rfc7636) grant.

After you have completed this lab you will have learned:

* that you can also use OAuth2 and OpenID Connect in a browser environment that does not provide any secure back-channel
* how to configure an Angular application to use OAuth2.0/OIDC with authorization code + [PKCE](https://tools.ietf.org/html/rfc7636) grant

## Folder Contents

Inside the folder _lab 6_ you can find 2 applications:

* __library-client-spa-initial__: This is the client application we will use as starting point for this lab
* __library-client-spa-complete__: This client application is the completed OAuth 2.0/OIDC client for this lab 

## Start the Lab

Now, let's start with Lab 6. Here we will implement the required additions to get a Single-Page-Application (SPA) that calls 
the resource server we have implemented in [lab 1](../lab1). This time we will use the [authorization code](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1) grant 
with the addition of [PKCE](https://tools.ietf.org/html/rfc7636).

We will use [Keycloak](https://keycloak.org) as identity provider.  
Please again make sure you have set up keycloak as described in the [Setup](../setup) section.

### Explore and run the initial application

First start the resource server application of Lab 1. If you could not complete the previous Lab yourself
then use and start the completed reference application 
in project [lab1/library-server-complete](../lab1/library-server-complete).

Start by downloading the necessary dependencies. 
Therefore, change to the __lab6/library-client-spa-initial__ folder using a terminal execute `npm install` via command line.

Then navigate your IDE of choice (suggesting VS Code or IntelliJ) to the __lab6/library-client-spa-initial__ project and at first explore this project a bit.
Then start the application by running `ng serve` on your commandline.

To see the application action open your browser on http://localhost:4200 (as shown in the terminal after issuing `ng serve`).

You will notice that the application starts up but in your browsers console you'll notice some failing HTTP requests when accessing the application. (should be 401 errors)
This is because there's no authentication to your IAM solution (Keycloak).

Now stop the client application again (Ctrl-C). You can leave the resource server running as we will need this after we have 
finished this client.

<hr>

### Step 1: Install the angular-oauth2-oidc library
  
In this step you're supposed to install the library, nothing else.
```
npm i angular-oauth2-oidc --save
```

This will install the latest version of [Manfred Steyer](https://github.com/manfredsteyer)'s OIDC certified OAuth 2.0 / OpenID Connect library for the Angular framework.

Next step is to import the library in your `app.module.ts` in the `imports` array.
```typescript
    OAuthModule.forRoot({
      resourceServer: {
        allowedUrls: ['http://localhost:9091/'],
        sendAccessToken: true
      }
    })
```

<hr>

### Step 2: Configure the library

The library we just installed gives us the ability to use the recommended authorization code grant with PKCE.

This allows us to use this improved authorization code grant without having a secure back-channel. 
As our application is executing in the user's browser, we are in an unsafe environment, meaning there's no secure channel the user 
(and by this means also an attacker) can eavesdrop.

Let's get started by creating a new service to encapsulate the authentication (and handle a few implementation quirks): `ng g s services/auth --skip-tests`

Now open the created file `services/auth.service.ts` and initialize the configuration object:

```typescript
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
    // The first three are defined by OIDC. The 4th is a use case specific one
    scope: 'openid profile'
  }
```
`disableAtHashCheck` is currently necessary as Keycloak does not include an `at_hash` claim in its id tokens. According to the [OIDC Core 1.0 3.1.3.6](https://openid.net/specs/openid-connect-core-1_0.html#CodeIDToken) this claim is optional.

After you've added this configuration to your service class (or as a constant to the file) you can start implementing the authentication. 
Start by adding instances of OAuthService and Router using dependency injection.

```typescript
  constructor(
    private oauthService: OAuthService,
    private router: Router
  ) {
```

Now start by adding a few subjects and observables to your class. These are needed to synchronize some function calls (e.g. checking token claims should only be done once authentication is completed) and prevent race conditions.
```typescript
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
```

Kudos to [Jeroen Heijmans](https://github.com/jeroenheijmans), who published an example on this library which takes care of multiple race condition problems. Major parts of this service are taken directly from his example.

Let's get started to set up the library in your `constructor()`-function:

```typescript
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
```
As you can see the configuration object we create before is put into the OAuthService. Afterwards the `tokenValidationHandler` is set to `NullValidationHandler`. This has one reason: The library currently has a bug which leads to being unable to disable the at_hash check with `JwksValidationHandler`. Once this has been fixed, you should NOT use `NullValidationHandler`.

`setupAutomaticSilentRefresh()` is used to enable background refreshing of the tokens once they exceed a percentage of their maximum lifetime. (by default 75%)

<hr>

### Step 3: Implementing the authentication triggering method

Next step is to implement the method that starts the authentication process. This process is split into multiple parts:

1. Get the OIDC discovery document as specified in [OpenID Connect Discovery 1.0 incorporating errata set 1](https://openid.net/specs/openid-connect-discovery-1_0.html)
2. Start the actual login procedure that redirects the user-agent to the authentication server discovered in 1. The library decides whether to use implicit or authorization code grant (with PKCE) by evaluating if `responseType: 'code'` was set.
3. After the login has taken place, the library will automatically store the tokens and resolve the promise. After resolving the promise we are publishing that the login __*is done loading*__. In addition the query params are cleared by navigating to `/`.

__NOTE:__ Currently it's not possible to keep the state when using authorization code grant with PKCE. This is a limitation by the library and will soon be fixed.

```typescript
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
```

After you implemented this function, you can use it in your `AppComponent` component (`app/app.component.ts`) :
```typescript
  constructor(private authService: AuthService) {
    this.authService.runInitialLoginSequence();
  }
```

<hr>

### Step 4: Add a few additional features

As you can now already see, you are directly forced to login, and your token is later on used to query APIs. But there are a few things missing:
- Your routes are not protected yet. Meaning by clever modification, users can access any part of your UI.
- The name of the user logged in is not shown.
- The logout button has no function at all

Let's get these bullet points fixed step-by-step.

#### Step 4a: Guarding routes

Start by generating guard classes using the Angular CLI `ng g g guards/auth` and `ng g g guards/bookCreate`. If you're asked, select `canActivate`. This will create two classes `app/guards/auth.guard.ts` and `app/guards/book-create.guard.ts`. Both should implement the `CanActivate`-interface. If not, add that manually.

We'll start by modifying the routing first (without having the guards implemented). Open your `app/app-routing.module.ts` and apply modifications (import necessary classes):

```typescript
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
```

Now you shouldn't be able to access any component (except the header) anymore. That's correct, let's get this fixed, starting with the `AuthGuard` component:
```typescript
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
```

Next we'll implement the `BookCreateGuard`, which will be less complicated:
```typescript
export class BookCreateGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
    ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    if(!this.authService.hasRole('LIBRARY_CURATOR')) {
      return this.router.navigate(['']);
    }
    return true;
  }
}
```

You'll see, that `hasRole` is missing in your `AuthService`. You can try to implement it on your own by fiddling with the id token, 
or you simply take my set of convenience methods and add them to your `app/services/auth.service.ts`:

```typescript
  public hasRole(role: string) {
    let claims: any = this.oauthService.getIdentityClaims();
    if (claims && claims.groups) {
      let roles: string[] = claims.groups;
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
```

Your routes should now be protected, but you still see the `Create Book` button, even if you're not authorized to do so. Let's fix this quickly. 
Go to `app/header/header.component.ts` and inject the `AuthService`:

```typescript
constructor(private authService: AuthService) { }
```

Now go to the template `app/header/header.component.html` and add a `ngIf` to the jumbotron at the bottom:

```typescript
 <div class="jumbotron" *ngIf="hasRole('LIBRARY_CURATOR') || hasRole('LIBRARY_ADMIN'">
     <a class="btn btn-primary" href="#" role="button" [routerLink]="['/createBook']" routerLinkActive="router-link-active">Create Book</a>
 </div>
```

If you're not an admin or a curator, you shouldn't see the whole jumbotron anymore by now.

#### Step 4b Show the user's name

As you already opened the file that needs to be modified (`app/header/header.component.ts`) if you followed the guide step-by-step, you can quickly add this feature. Simply fill the `fullname` attribute on init: 

```typescript
  ngOnInit() {
    this.authService.isDoneLoading$.subscribe(_ => {
      this.fullname = this.authService.getFullname();
    });
  }

  logout() {
    this.authService.logout();
  }

  hasRole(role: string): boolean {
    return this.authService.hasRole(role);
  }
```

As you can see we wait until the authService has finished processing, so we can safely access the attribute.

#### Step 4c Enable logout

As in Step 4b, you'll need to modify the `HeaderComponent`. Try to implement the `logout()`-function yourself. 😉

### Run the completed application

Now it is time to see the completed application running in action.

First make sure you still have Keycloak running, and you have started the 
resource server application of Lab 1 ([lab1/library-server-complete](../lab1/library-server-complete)).

Then start the application by running `ng serve` on your commandline.

To see the application action open your browser on http://localhost:4200 (as shown in the terminal after issuing `ng serve`).

You will notice that the application starts up, and your browser will automatically redirect to our IAM solution (Keycloak) to authenticate the user.

By using the [authorization code](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1) grant + [PKCE](https://tools.ietf.org/html/rfc7636) instead of the 
[implicit grant](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.2) we reduced risks for token leakage pretty much.
The only drawback we are still facing is the fact that the library stores the tokens in browser local storage which is not a safe place
in case of a cross site scripting vulnerability.

Please consult the latest [OAuth 2.0 for Browser-Based Apps](https://tools.ietf.org/html/draft-ietf-oauth-browser-based-apps) draft for more details on this.

This concludes our lab on securing a SPA with OAuth 2.0 and OpenID Connect.