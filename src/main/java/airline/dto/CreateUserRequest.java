package airline.dto;

import airline.entity.enums.UserRole;

public class CreateUserRequest {

  private String name;

  private UserRole role;

    // Constructor

    public CreateUserRequest(String name, UserRole role) {
      this.name = name;
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

  
  
}
