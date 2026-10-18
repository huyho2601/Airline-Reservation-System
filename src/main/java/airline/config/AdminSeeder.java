package airline.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import airline.dto.CreateUserRequest;
import airline.repository.UserRepository;
import airline.service.UserService;

@Component 
public class AdminSeeder implements CommandLineRunner {

  private final UserRepository userRepository;
  private final UserService userService;
  private final String adminUsername;
  private final String adminEmail;
  private final String adminPassword;

  private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

  public AdminSeeder(
      UserRepository userRepository,
      UserService userService,
      @Value("${app.admin.username}") String adminUsername,
      @Value("${app.admin.email}") String adminEmail,
      @Value("${app.admin.password}") String adminPassword) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.adminUsername = adminUsername;
    this.adminEmail = adminEmail;
    this.adminPassword = adminPassword;
  }

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.existsByUsername(adminUsername)) {
      log.info("Admin '{}' already exists, skipping seed", adminUsername);
      return;
    }

    // build the CreateUserRequest, then:
    CreateUserRequest request = new CreateUserRequest("Admin", adminUsername, adminPassword, adminEmail);
    userService.createAdmin(request);
    log.info("Admin '{}' created", adminUsername);
  }
  
}
