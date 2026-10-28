package airline.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import airline.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthPageController {

  private final UserService userService;

  public AuthPageController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login")
  public String login() {
    return "auth/login";
  }

  @GetMapping("/register")
  public String register() {
    return "auth/register";
  }

}
