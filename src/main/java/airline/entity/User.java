package airline.entity;

import airline.entity.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "username", nullable = false)
  private String username;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role;

  // Constructors
  public User() {
  }

  public User(String username, UserRole role) {
    this.username = username;
    this.role = role;
  }

  // Getters and Setters
  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return username;
  }

  public void setName(String username) {
    this.username = username;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }
  


}
