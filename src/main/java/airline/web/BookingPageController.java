package airline.web;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import airline.entity.Booking;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.User;
import airline.security.CustomUserDetails;
import airline.service.BookingService;
import airline.service.SeatService;
import airline.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingPageController {

  private final UserService userService;
  private final BookingService bookingService;
  private final SeatService seatService;

  public BookingPageController(UserService userService, BookingService bookingService, SeatService seatService) {
    this.userService = userService;
    this.bookingService = bookingService;
    this.seatService = seatService;
  }

  @GetMapping("/bookings/my")
  public String myBookings(
      @AuthenticationPrincipal CustomUserDetails principal,
      Model model) {

    User user = userService.getUserByUserName(principal.getUsername());

    List<Booking> bookings = bookingService.getBookingsForUser(user);

    model.addAttribute("bookings", bookings);

    return "bookings/myBookings";
  }

  @PostMapping("/bookings/my/{bookingId}/cancel")
  public String cancelBooking(
      @PathVariable String bookingId,
      @AuthenticationPrincipal CustomUserDetails principal) {

    long id = Long.parseLong(bookingId);

    User user = userService.getUserByUserName(principal.getUsername());

    bookingService.deleteBooking(id, user);

    return "redirect:/bookings/my";
  }

  @GetMapping("/bookings/my/{bookingId}/changeSeat")
  public String editBooking(
      @PathVariable String bookingId,
      @AuthenticationPrincipal CustomUserDetails principal,
      Model model) {

    long idLong = Long.parseLong(bookingId);
    User user = userService.getUserByUserName(principal.getUsername());
    Booking booking = bookingService.getBooking(idLong, user);
    Flight flight = booking.getSeat().getFlight();
    TreeMap<Integer, ArrayList<Seat>> seatMap = seatService.getSeatMap(flight.getFlightNumber());


    model.addAttribute("seatMap", seatMap);
    model.addAttribute("changeBooking", booking);
    model.addAttribute("flight", flight);

    return "flights/details";
  }

}
