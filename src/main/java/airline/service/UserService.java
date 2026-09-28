package airline.service;

import airline.dto.CreateUserRequest;
import airline.dto.UpdateUserRequest;
import airline.entity.User;
import java.util.List;

public interface UserService {
  List<User> getAllUsers();

  User getUserByName(String name);

  User getUserById(long userId);

  public User createUser(CreateUserRequest user);

  public User createAdmin(CreateUserRequest userRequest);

  User updateUserName(long userId, UpdateUserRequest request);

  void deleteUser(Long id);
}
