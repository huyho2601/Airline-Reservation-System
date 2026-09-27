package airline.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import airline.dto.CreateUserRequest;
import airline.entity.User;
import airline.service.UserService;
import airline.service.implementation.UserImp;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/user_service")
public class UserController {

  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/all_users")
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{userName}")
  public User getUserByName(@PathVariable String userName) {
    return userService.getUserByName(userName);
  }

  @PostMapping
  public ResponseEntity createUser(@RequestBody CreateUserRequest request) {
    User newUser = userService.createUser(request);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("{userName}")
        .buildAndExpand(newUser.getUsername())
        .toUri();

    return ResponseEntity.created(location).body(newUser);
  }

}
