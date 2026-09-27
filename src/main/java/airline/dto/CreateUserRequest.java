package airline.dto;

import airline.entity.enums.UserRole;

public class CreateUserRequest {

  private String name;
  private String password;
  private String email;
  private UserRole role;

    // Constructor

    public CreateUserRequest(String name, String password, String email, UserRole role) {
      this.name = name;
      this.password = password;
      this.email = email;
      this.role = role;
    }
    

    // Getters and Setters

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  
  
}
