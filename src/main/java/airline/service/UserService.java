package airline.service;

import airline.dto.CreateUserRequest;
import airline.dto.PasswordChangeRequest;
import airline.dto.UpdateUserRequest;
import airline.entity.User;
import airline.entity.enums.UserRole;

import java.util.List;

public interface UserService {
  List<User> getAllUsers();

  User getUserByUserName(String username);

  User getUserById(long userId);

  List<User> getUserByName(String name);

  List<User> getUserByRole(UserRole role);

  public User createUser(CreateUserRequest user);

  public User createAdmin(CreateUserRequest userRequest);

  User updateUser(long userId, UpdateUserRequest request);

  User updateUserPassword(long userId, PasswordChangeRequest request);

  void deleteUser(Long id);
}
