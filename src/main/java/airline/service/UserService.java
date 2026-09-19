package airline.service;

import airline.entity.User;
import java.util.List;

public interface UserService {
  List<User> getAllUsers();
  User getUserByName(String name);
  User createUser(User user);
  User updateUser(User user);
  void deleteUser(Long id);
}
