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
public class UserImp implements UserService {

  private static final String USERNOTFOUND = "User not found: ";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
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
  public User updateUserPassword(long userId, PasswordChangeRequest request)
  {
    User currentUser = getUserById(userId);

    String currentHashedPassword = currentUser.getPassword();
    String inputPassword = request.currentPassword();

    if (!passwordEncoder.encode(inputPassword).matches(currentHashedPassword)) {
      throw new InvalidCredentialsException("Password not matched");
    }

    currentUser.setPassword(passwordEncoder.encode(request.newPassword()));

    return userRepository.save(currentUser);
  }

  @Override
  public void deleteUser(Long id) {
    User currentUser = getUserById(id);
    userRepository.delete(currentUser);
  }

  @Override
  public User createAdmin(CreateUserRequest userRequest) {

    User newUser = new User();

    newUser.setUsername(userRequest.getName());
    newUser.setUserEmail(userRequest.getEmail());
    newUser.setPassword(passwordEncoder.encode(userRequest.getPassword())); // hash password
    newUser.setRole(UserRole.ADMIN);

    return userRepository.save(newUser);
  }

}
