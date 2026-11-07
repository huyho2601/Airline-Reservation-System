package airline.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import airline.dto.CreateBookingRequest;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.User;
import airline.error.SeatUnavailableException;
import airline.security.CustomUserDetails;
import airline.service.BookingService;
import airline.service.FlightService;
import airline.service.SeatService;
import airline.service.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SeatPageController {

  private final FlightService flightService;
  private final SeatService seatService;
  private final BookingService bookingService;
  private final UserService userService;

  public SeatPageController(FlightService flightService, SeatService seatService, BookingService bookingService,
      UserService userService) {
    this.flightService = flightService;
    this.seatService = seatService;
    this.bookingService = bookingService;
    this.userService = userService;
  }

  @GetMapping("/flights/{flightNumber}/book")
  public String showBooking(
      @PathVariable String flightNumber,
      @RequestParam String seat,
      @AuthenticationPrincipal CustomUserDetails principal,
      Model model) {

    User user = principal.getUser();
    Flight flight = flightService.getFlightByFlightNumber(flightNumber);
    Seat seatObj = seatService.getSeat(flightNumber, seat);

    model.addAttribute("seat", seatObj);
    model.addAttribute("user", user);
    model.addAttribute("flight", flight);

    return "bookings/confirm";
  }

  @PostMapping("/flights/{flightNumber}/book")
  public String confirmBooking(
      @PathVariable String flightNumber,
      @RequestParam String seat,
      @AuthenticationPrincipal CustomUserDetails principal,
      RedirectAttributes redirect) {

    User user = userService.getUserByUserName(principal.getUsername());

    try {
      CreateBookingRequest request = new CreateBookingRequest(flightNumber, seat);
      bookingService.createBooking(request, user);
    } catch (SeatUnavailableException e) {
      redirect.addFlashAttribute("error", "Seat {seat}was just taken. Please choose another seat.");
      return "redirect:/flights/{flightNumber}";
    }

    redirect.addFlashAttribute("success", "Booking Confirmed!");

    return "redirect:/bookings/my";
  }

}
