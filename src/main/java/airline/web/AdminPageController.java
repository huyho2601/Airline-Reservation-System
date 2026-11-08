package airline.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import airline.dto.AdminFlightRow;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.security.CustomUserDetails;
import airline.service.FlightService;
import airline.service.SeatService;

@Controller
public class AdminPageController {

  private final FlightService flightService;
  private final SeatService seatService;

  public AdminPageController(FlightService flightService, SeatService seatService) {
    this.flightService = flightService;
    this.seatService = seatService;
  }

  @GetMapping("/admin/flights")
  public String manageFlight(
      @RequestParam(required = false) String flightNumber,
      @RequestParam(defaultValue = "all") String filter,
      Model model) {

    List<Flight> flights = new ArrayList<>();
    List<AdminFlightRow> rows = new ArrayList<>();

    model.addAttribute("flightNumber", flightNumber);
    model.addAttribute("filter", filter);

    if (flightNumber == null || flightNumber.isBlank()) {
      switch (filter) {
        case "upcoming":

          break;

        case "past":
          break;

        case "all":
          flights = flightService.getAllFlights();
          break;
        default:

          break;
      }
    } else {
      Flight search = flightService.getFlightByFlightNumber(flightNumber);
      if (search != null) {
        flights.add(search);
      }
    }

    for (Flight entry : flights) {
      long total = seatService.getAllSeats(entry.getFlightNumber()).size();
      long bookedSeat = getBookedSeats(entry.getFlightNumber(), total);
      AdminFlightRow row = new AdminFlightRow(entry, bookedSeat, total);
      rows.add(row);
    }

    model.addAttribute("rows", rows);

    return "admin/flights";
  }

  private long getBookedSeats(String flightNumber, long total) {

    List<Seat> availableSeats = seatService.getAvailableSeats(flightNumber);
    long available = availableSeats.size();

    return total - available;
  }

}
