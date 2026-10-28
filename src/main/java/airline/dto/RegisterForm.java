package airline.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {

  @NotBlank (message = "Name is required!")
  private String fullName;

  @Email
  @NotBlank (message = "Email is required!")
  private String email;

  @NotBlank (message = "Username is required!")
  private String username;

  @NotBlank 
  @Size (min = 8, message = "Password must be least 8 characters long!")
  private String password;

  @NotBlank 
  @Size (min = 8, message = "Password must be least 8 characters long!")
  private String confirmPassword;


  // Constructor
  public RegisterForm() {
  }

  // Getters and setters
  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmPassword() {
    return confirmPassword;
  }

  public void setConfirmPassword(String confirmPassword) {
    this.confirmPassword = confirmPassword;
  }

  
}
