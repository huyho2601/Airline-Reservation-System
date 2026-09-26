package airline.service;

import airline.dto.CreateUserRequest;
import airline.entity.User;
import java.util.List;

public interface UserService {
  List<User> getAllUsers();
  User getUserByName(String name);
  public User createUser(CreateUserRequest user);
  User updateUser(User user);
  void deleteUser(Long id);
}
