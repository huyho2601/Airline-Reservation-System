package airline.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.User;
import airline.security.CustomUserDetails;
import airline.service.FlightService;
import airline.service.SeatService;

@Controller
public class SeatPageController {

  private final FlightService flightService;
  private final SeatService seatService;

  public SeatPageController(FlightService flightService, SeatService seatService) {
    this.flightService = flightService;
    this.seatService = seatService;
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

}
