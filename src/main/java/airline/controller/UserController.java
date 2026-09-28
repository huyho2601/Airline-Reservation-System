package airline.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import airline.dto.CreateUserRequest;
import airline.dto.PasswordChangeRequest;
import airline.dto.UpdateUserRequest;
import airline.entity.User;
import airline.service.UserService;
import airline.service.implementation.UserImp;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

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

  @GetMapping("/{userId}")
  public User getUserById(@PathVariable long userId) {
    return userService.getUserById(userId);
  }

  @PostMapping
  public ResponseEntity createUser(@RequestBody CreateUserRequest request) {
    User newUser = userService.createUser(request);

    URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/api/users/{userId}")
        .buildAndExpand(newUser.getId())
        .toUri();

    return ResponseEntity.created(location).body(newUser);
  }

  @PostMapping("/admin")
  public ResponseEntity createAdmin(@RequestBody CreateUserRequest request) {
    User newUser = userService.createAdmin(request);

    URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/api/users/{userId}")
        .buildAndExpand(newUser.getId())
        .toUri();

    return ResponseEntity.created(location).body(newUser);
  }

  @PutMapping("/{userId}")
  public ResponseEntity updateUser(
      @PathVariable long userId,
      @RequestBody UpdateUserRequest request) {

    User updatedUser = userService.updateUser(userId, request);

    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }

  @PutMapping("/{userId}/password")
  public ResponseEntity<Void> changePassword(
          @PathVariable long userId,
          @Valid @RequestBody PasswordChangeRequest request) {
      userService.updateUserPassword(userId, request);
      return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity deleteUser(@PathVariable long userId) {
    userService.deleteUser(userId);

    return new ResponseEntity<>("Deleted sucessfully", HttpStatus.OK);
  }

}
