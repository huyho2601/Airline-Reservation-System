package airline.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import airline.dto.CreateUserRequest;
import airline.dto.PasswordChangeRequest;
import airline.dto.UpdateUserRequest;
import airline.entity.User;
import airline.entity.enums.UserRole;
import airline.error.InvalidCredentialsException;
import airline.error.ResourceNotFoundException;
import airline.repository.UserRepository;
import airline.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImp implements UserService {

  private static final String USERNOTFOUND = "User not found: ";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  public User getUserByUserName(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException(USERNOTFOUND + username));

    return user;
  }

  @Override
  public List<User> getUserByName(String name) {
    return userRepository.findByNameContaining(name);
  }

  @Override
  public User getUserById(long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException(USERNOTFOUND + userId));

    return user;
  }

  @Override
  @Transactional 
  public User createUser(CreateUserRequest userRequest) {

    // Check for duplicate
    if (userRepository.existsByUsername(userRequest.getUsername())) {
      throw new InvalidCredentialsException("Username already exists: " + userRequest.getUsername());
    }
    if (userRepository.existsByUserEmail(userRequest.getEmail())) {
      throw new InvalidCredentialsException("Email already exists: " + userRequest.getEmail());
    }
    

    // Create new user instance
    User newUser = new User();
    newUser.setName(userRequest.getName());
    newUser.setUsername(userRequest.getUsername());
    newUser.setUserEmail(userRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(userRequest.getPassword())); // hash password
    newUser.setRole(UserRole.CLIENT);

    return userRepository.save(newUser);
  }

  @Override
  @Transactional
  public User updateUser(long userId, UpdateUserRequest request) {
    User curentUser = getUserById(userId);

    curentUser.setUsername(request.username());
    curentUser.setUserEmail(request.email());

    return userRepository.save(curentUser);
  }

  @Override
  @Transactional
  public User updateUserPassword(long userId, PasswordChangeRequest request) {
    User currentUser = getUserById(userId);

    if (!passwordEncoder.matches(request.currentPassword(), currentUser.getPassword())) {
      throw new InvalidCredentialsException("Password not matched");
    }

    currentUser.setPassword(passwordEncoder.encode(request.newPassword()));

    return userRepository.save(currentUser);
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    User currentUser = getUserById(id);
    userRepository.delete(currentUser);
  }

  @Override
  @Transactional 
  public User createAdmin(CreateUserRequest userRequest) {

    User newUser = new User();
    newUser.setName(userRequest.getName());
    newUser.setUsername(userRequest.getUsername());
    newUser.setUserEmail(userRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(userRequest.getPassword())); // hash password
    newUser.setRole(UserRole.ADMIN);

    return userRepository.save(newUser);
  }

}
