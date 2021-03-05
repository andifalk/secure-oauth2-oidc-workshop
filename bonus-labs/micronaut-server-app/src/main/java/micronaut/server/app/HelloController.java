package micronaut.server.app;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;

import java.util.Map;

@Secured("isAuthenticated()")
@Controller("/hello")
public class HelloController {

  @Get
  public String sayHello(Authentication authentication) {
    Map<String, Object> claims = authentication.getAttributes();
    return "it works for user: " + claims.get("name") + " (" + claims.get("email") + ")";
  }
}
