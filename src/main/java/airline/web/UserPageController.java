package airline.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import airline.entity.Booking;
import airline.entity.Flight;
import airline.entity.User;
import airline.security.CustomUserDetails;
import airline.service.BookingService;
import airline.service.FlightService;
import airline.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
        Model model
  ) {
    User user = userService.getUserByUserName(principal.getUsername());
    int totalBooking = bookingService.getBookingsForUser(user).size();

    Booking tempBooking = bookingService.getEarliestFlight(user);
    Flight earliestFlight = tempBooking != null ? tempBooking.getSeat().getFlight() : null;

    model.addAttribute("user", user);
    model.addAttribute("totalBookings", totalBooking);
    model.addAttribute("earliestFlight", earliestFlight);
    
      return "user/profile";
  }
  
}
