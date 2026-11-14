package airline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeRequest {

  @NotBlank
  String currentPassword;

  @NotBlank
  @Size(min = 8, max = 128)
  String newPassword;

  public PasswordChangeRequest(){}

  public PasswordChangeRequest(@NotBlank String currentPasswor,
      @NotBlank @Size(min = 8, max = 128) String newPassword) {
    this.currentPassword = currentPasswor;
    this.newPassword = newPassword;
  }

  public String getCurrentPassword() {
    return currentPassword;
  }

  public void setCurrentPassword(String currentPasswor) {
    this.currentPassword = currentPasswor;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

}
