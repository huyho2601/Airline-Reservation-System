package airline.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import airline.dto.CreateUserRequest;
import airline.dto.UpdateUserNameRequest;
import airline.entity.User;
import airline.error.ResourceNotFoundException;
import airline.repository.UserRepository;
import airline.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;


@Service
public class UserImp implements UserService {

  private String USERNOTFOUND = "User not found: ";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

public UserImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
  this.userRepository = userRepository;
  this.passwordEncoder = passwordEncoder;
}

  @Override
  public List<User> getAllUsers() {
    return (List<User>) userRepository.findAll();
  }

  @Override
  public User getUserByName(String name) {
    User user = userRepository.findByUsername(name)
        .orElseThrow(() -> new ResourceNotFoundException(USERNOTFOUND + name));

    return user;
  }

  @Override
  public User getUserById(long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USERNOTFOUND + userId));

    return user;
  }

  @Override
  public User createUser(CreateUserRequest userRequest) {

    User newUser = new User();

    newUser.setUsername(userRequest.getName());
    newUser.setUserEmail(userRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(userRequest.getPassword())); // hash password
    newUser.setRole(userRequest.getRole());

    return userRepository.save(newUser);
  }

  @Override
  public User updateUserName(long userId, UpdateUserNameRequest request) {
    User curentUser = getUserById(userId);

    curentUser.setUsername(request.getUsername());

    return userRepository.save(curentUser);
  }

  @Override
  public void deleteUser(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
  }

}
