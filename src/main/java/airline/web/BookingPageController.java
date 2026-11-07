package airline.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import airline.entity.Booking;
import airline.entity.User;
import airline.security.CustomUserDetails;
import airline.service.BookingService;
import airline.service.UserService;

@Controller 
public class BookingPageController {

  private final UserService userService;
  private final BookingService bookingService;

  public BookingPageController(UserService userService, BookingService bookingService) {
    this.userService = userService;
    this.bookingService = bookingService;
  }

  @GetMapping("bookings/my")
  public String myBookings(
      @AuthenticationPrincipal CustomUserDetails principal,
      Model model) {

    User user = userService.getUserByUserName(principal.getUsername());

    List<Booking> bookings = bookingService.getBookingsForUser(user);

    model.addAttribute("bookings", bookings);

    return "bookings/myBookings";
  }
}
