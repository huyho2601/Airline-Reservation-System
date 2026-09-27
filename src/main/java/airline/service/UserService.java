package airline.service;

import airline.dto.CreateUserRequest;
import airline.dto.UpdateUserNameRequest;
import airline.entity.User;
import java.util.List;

public interface UserService {
  List<User> getAllUsers();

  User getUserByName(String name);

  User getUserById(long userId);

  public User createUser(CreateUserRequest user);

  User updateUserName(long userId, UpdateUserNameRequest request);

  void deleteUser(Long id);
}
