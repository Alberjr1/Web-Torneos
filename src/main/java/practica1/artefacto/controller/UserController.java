package practica1.artefacto.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @GetMapping("/me")
  public Map<String,Object> me(Authentication auth) {
    return Map.of(
      "username", auth.getName(),
      "roles", auth.getAuthorities().stream()
                  .map(GrantedAuthority::getAuthority)
                  .toList()
    );
  }
}

