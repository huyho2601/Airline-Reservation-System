package airline.dto;

public class UpdateUserNameRequest {

  private String username;

  public UpdateUserNameRequest(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

}
