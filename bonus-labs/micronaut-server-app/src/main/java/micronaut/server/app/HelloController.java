package micronaut.server.app;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.token.jwt.validator.AuthenticationJWTClaimsSetAdapter;

import java.security.Principal;
import java.util.Map;

@Secured("isAuthenticated()")
@Controller("/hello")
public class HelloController {

  @Get
  public String sayHello(Principal principal) {
    AuthenticationJWTClaimsSetAdapter jwtClaimsSetAdapter =
        (AuthenticationJWTClaimsSetAdapter) principal;
    Map<String, Object> claims = jwtClaimsSetAdapter.getAttributes();

    return "it works for user: " + claims.get("name") + " (" + claims.get("email") + ")";
  }
}
