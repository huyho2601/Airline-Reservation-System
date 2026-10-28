package airline.web;

import java.lang.ProcessBuilder.Redirect;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import airline.dto.CreateUserRequest;
import airline.dto.RegisterForm;
import airline.error.DuplicateResourceException;
import airline.service.UserService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
  public String registerForm(Model model) {
    RegisterForm registerForm = new RegisterForm();
    model.addAttribute("form", registerForm);
    return "auth/register";
  }

  @PostMapping("/register")
  public String register(
      @Valid @ModelAttribute("form") RegisterForm registerForm,
      BindingResult result,
      RedirectAttributes redirect) {

    if (!Objects.equals(registerForm.getPassword(), registerForm.getConfirmPassword())) {
      result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
    }

    if (result.hasErrors()) {
      return "auth/register";
    }

    try {
      CreateUserRequest newUser = new CreateUserRequest(
          registerForm.getFullName(),
          registerForm.getUsername(),
          registerForm.getPassword(),
          registerForm.getEmail());
      userService.createUser(newUser);

    } catch (DuplicateResourceException e) {
      result.reject("duplicate", "Username and/or email have been taken!");
      return "auth/register";
    }

    redirect.addFlashAttribute("success", "Account created, please log in");
    return "redirect:/login";
  }

}
