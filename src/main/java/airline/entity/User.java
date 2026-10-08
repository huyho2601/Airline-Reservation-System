package airline.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import airline.entity.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "username", nullable = false, unique = true)
  private String username;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  @Column(name = "email", nullable = false, unique = true)
  private String userEmail;

  @JsonIgnore
  @Column(name = "password", nullable = false)
  private String password;

  // Constructors
  public User() {
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public User(String name, String username, UserRole role, String userEmail, String password) {
    this.name = name;
    this.username = username;
    this.role = role;
    this.userEmail = userEmail;
    this.password = password;
  }

  // Getters and Setters
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

}
