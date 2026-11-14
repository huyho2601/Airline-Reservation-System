package airline.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import airline.dto.PasswordChangeRequest;
import airline.dto.UpdateUserRequest;
import airline.entity.Booking;
import airline.entity.Flight;
import airline.entity.User;
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
    int totalBooking = bookingService.getBookingsForUser(user).size();

    Booking tempBooking = bookingService.getEarliestFlight(user);
    Flight earliestFlight = tempBooking != null ? tempBooking.getSeat().getFlight() : null;

    UpdateUserRequest infoForm = new UpdateUserRequest(
        user.getName(),
        user.getUserEmail());

    PasswordChangeRequest passwordForm = new PasswordChangeRequest();

    model.addAttribute("infoForm", infoForm);
    model.addAttribute("passwordForm", passwordForm);
    model.addAttribute("user", user);
    model.addAttribute("totalBookings", totalBooking);
    model.addAttribute("earliestFlight", earliestFlight);

    return "user/profile";
  }

  @PostMapping("/profile/info")
  public String changeProfile(
      @ModelAttribute("infoForm") UpdateUserRequest form,
      @AuthenticationPrincipal CustomUserDetails principal,
      BindingResult result,
      RedirectAttributes redirect) {

    User user = userService.getUserByUserName(principal.getUsername());
    userService.updateUser(user.getId(), form);

    redirect.addFlashAttribute("success", "The information has been updated sucessfully!");
    return "redirect:/profile";
  }

  @PostMapping("/profile/password")
  public String changePassword(
      @ModelAttribute("passwordForm") PasswordChangeRequest form,
      @AuthenticationPrincipal CustomUserDetails principal,
      BindingResult result,
      RedirectAttributes redirect) {

    User user = userService.getUserByUserName(principal.getUsername());
    userService.updateUserPassword(user.getId(), form);

    redirect.addFlashAttribute("success", "Password has been changed sucessfully!");
    return "redirect:/profile";
  }

}
