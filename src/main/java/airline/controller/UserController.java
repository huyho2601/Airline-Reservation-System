package airline.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import airline.dto.CreateUserRequest;
import airline.dto.UpdateUserRequest;
import airline.entity.User;
import airline.service.UserService;
import airline.service.implementation.UserImp;

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
  public User getUserById(@PathVariable long useriD) {
    return userService.getUserById(useriD);
  }

  @PostMapping
  public ResponseEntity createUser(@RequestBody CreateUserRequest request) {
    User newUser = userService.createUser(request);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("{userId}")
        .buildAndExpand(newUser.getId())
        .toUri();

    return ResponseEntity.created(location).body(newUser);
  }

  @PutMapping("/{userId}")
  public ResponseEntity putMethodName(
      @PathVariable long userId,
      @RequestBody UpdateUserRequest request) {
    
    User updatedUser = userService.updateUserName(userId, request);

    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity deleteUser(@PathVariable long userId) {
    userService.deleteUser(userId);

    return new ResponseEntity<>("Deleted sucessfully", HttpStatus.OK);
  }

}
