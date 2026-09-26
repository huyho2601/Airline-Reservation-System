package airline.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import airline.dto.CreateUserRequest;
import airline.entity.User;
import airline.error.ResourceNotFoundException;
import airline.repository.UserRepository;
import airline.service.UserService;

@Service 
public class UserImp implements UserService {

  private String USERNOTFOUND = "User not found: ";

  private UserRepository userRepository;

  public UserImp(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<User> getAllUsers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
  }

  @Override
  public User getUserByName(String name) {
    User user = userRepository.findByUsername(name)
        .orElseThrow(() -> new ResourceNotFoundException(USERNOTFOUND + name));
      
    return user;
  }

  @Override
  public User createUser(CreateUserRequest user) {
    
    User newUser = new User();

    newUser.setName(user.getName());
    newUser.setRole(user.getRole());

    return userRepository.save(newUser);
  }

  @Override
  public User updateUser(User user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
  }

  @Override
  public void deleteUser(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
  }
  
}
