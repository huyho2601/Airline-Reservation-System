package airline.web;

import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import airline.dto.ChangePasswordForm;
import airline.dto.PasswordChangeRequest;
import airline.dto.UpdateUserRequest;
import airline.entity.Booking;
import airline.entity.Flight;
import airline.entity.User;
import airline.error.DuplicateResourceException;
import airline.error.InvalidCredentialsException;
import airline.security.CustomUserDetails;
import airline.service.BookingService;
import airline.service.FlightService;
import airline.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Helper inner class
class Helper {
  int totalBookings;
  UpdateUserRequest infoForm;
  Flight earlyFlight;

  public Helper() {
  }

  public Helper(int totalBookings, UpdateUserRequest infoForm, Flight earlyFlight) {
    this.totalBookings = totalBookings;
    this.infoForm = infoForm;
    this.earlyFlight = earlyFlight;
  }

  public int getTotalBookings() {
    return totalBookings;
  }

  public void setTotalBookings(int totalBookings) {
    this.totalBookings = totalBookings;
  }

  public UpdateUserRequest getInfoForm() {
    return infoForm;
  }

  public void setInfoForm(UpdateUserRequest infoForm) {
    this.infoForm = infoForm;
  }

  public Flight getEarlyFlight() {
    return earlyFlight;
  }

  public void setEarlyFlight(Flight earlyFlight) {
    this.earlyFlight = earlyFlight;
  }

}

@Controller
public class UserPageController {

  private final UserService userService;
  private final BookingService bookingService;
  private final FlightService flightService;

  public UserPageController(UserService userService, BookingService bookingService, FlightService flightService) {
    this.userService = userService;
    this.bookingService = bookingService;
    this.flightService = flightService;
  }

  @GetMapping("/profile")
  public String getProfile(
      @AuthenticationPrincipal CustomUserDetails principal,
      Model model) {
    User user = userService.getUserByUserName(principal.getUsername());

    Helper helper = addPage(user);
    int totalBookings = helper.getTotalBookings();
    Flight earliestFlight = helper.getEarlyFlight();
    UpdateUserRequest infoForm = helper.getInfoForm();

    ChangePasswordForm passwordForm = new ChangePasswordForm();

    model.addAttribute("infoForm", infoForm);
    model.addAttribute("passwordForm", passwordForm);
    model.addAttribute("user", user);
    model.addAttribute("totalBookings", totalBookings);
    model.addAttribute("earliestFlight", earliestFlight);

    return "user/profile";
  }

  @PostMapping("/profile/info")
  public String changeProfile(
      @ModelAttribute("infoForm") UpdateUserRequest form,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails principal,
      RedirectAttributes redirect,
      Model model) {

    User user = userService.getUserByUserName(principal.getUsername());
    if (!result.hasErrors()) {
      try {
        String name = form.name().trim();
        String email = form.email().trim();
        UpdateUserRequest trimmedForm = new UpdateUserRequest(name, email);
        userService.updateUser(user.getId(), trimmedForm);
      } catch (DuplicateResourceException e) {
        result.rejectValue("email", "duplicate", "This email is already used by another account");
      }

      redirect.addFlashAttribute("success", "The information has been updated sucessfully!");
    }

    if (result.hasErrors()) {
      Helper helper = addPage(user);
      int totalBookings = helper.getTotalBookings();
      Flight earliestFlight = helper.getEarlyFlight();

      model.addAttribute("passwordForm", new ChangePasswordForm());
      model.addAttribute("user", user);
      model.addAttribute("totalBookings", totalBookings);
      model.addAttribute("earliestFlight", earliestFlight);

      return "user/profile";
    }

    return "redirect:/profile";
  }

  @PostMapping("/profile/password")
  public String changePassword(
      @ModelAttribute("passwordForm") ChangePasswordForm form,
      BindingResult result,
      @AuthenticationPrincipal CustomUserDetails principal,
      RedirectAttributes redirect,
      Model model) {

    User user = userService.getUserByUserName(principal.getUsername());

    if (!Objects.equals(form.getNewPassword(), form.getConfirmPassword())) {
      result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
    }

    if (!result.hasErrors()) {
      try {
        PasswordChangeRequest request = new PasswordChangeRequest(form.getCurrentPassword(), form.getNewPassword());
        userService.updateUserPassword(user.getId(), request);
      } catch (InvalidCredentialsException e) {
        result.rejectValue("currentPassword", "wrong", "Current password is incorrect!");
      }
    }

    if (result.hasErrors()) {
      // Prevent passwords from being sent back to browser
      form.setCurrentPassword(null);
      form.setNewPassword(null);
      form.setConfirmPassword(null);

      Helper helper = addPage(user);
      int totalBookings = helper.getTotalBookings();
      Flight earliestFlight = helper.getEarlyFlight();
      UpdateUserRequest infoForm = helper.getInfoForm();

      model.addAttribute("infoForm", infoForm);
      model.addAttribute("user", user);
      model.addAttribute("totalBookings", totalBookings);
      model.addAttribute("earliestFlight", earliestFlight);

      return "user/profile";
    }

    redirect.addFlashAttribute("success", "Password has been changed sucessfully!");
    return "redirect:/profile";
  }

  private Helper addPage(User user) {

    int totalBookings = bookingService.getBookingsForUser(user).size();

    Booking tempBooking = bookingService.getEarliestFlight(user);
    Flight earliestFlight = tempBooking != null ? tempBooking.getSeat().getFlight() : null;

    UpdateUserRequest infoForm = new UpdateUserRequest(
        user.getName(),
        user.getUserEmail());

    Helper helper = new Helper(totalBookings, infoForm, earliestFlight);

    return helper;
  }

}
