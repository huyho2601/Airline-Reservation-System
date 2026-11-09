package airline.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import airline.dto.AdminFlightRow;
import airline.dto.CreateFlightRequest;
import airline.dto.RegisterForm;
import airline.dto.UpdateFlightRequest;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.security.CustomUserDetails;
import airline.service.FlightService;
import airline.service.SeatService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
          flights = flightService.getUpcomingFlights();
          break;

        case "past":
          flights = flightService.getPastFlights();
          break;

        case "all":
          flights = flightService.getAllFlights();
          break;
        default:
          flights = flightService.getAllFlights();
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

  @GetMapping("/admin/flights/{flightNumber}/edit")
  public String getEditPage(
      @PathVariable String flightNumber,
      Model model) {

    Flight flight = flightService.getFlightByFlightNumber(flightNumber);

    if (flight == null) {
      return "redirect:/admin/flights";
    }

    UpdateFlightRequest form = new UpdateFlightRequest();
    form.setOrigin(flight.getOrigin());
    form.setDestination(flight.getDestination());
    form.setDepartureTime(flight.getDepartureTime());
    form.setArrivalTime(flight.getArrivalTime());
    form.setPrice(flight.getPrice());

    model.addAttribute("form", form);
    model.addAttribute("flightNumber", flightNumber);

    return "admin/edit";
  }

  @PostMapping("/admin/flights/{flightNumber}/edit")
  public String editFlight(
      @PathVariable String flightNumber,
      @Valid @ModelAttribute("form") UpdateFlightRequest filledForm,
      BindingResult result,
      RedirectAttributes redirect) {

    if (result.hasErrors()) {
      return "admin/edit";
    }

    flightService.updateFlight(flightNumber, filledForm);

    redirect.addFlashAttribute("success", "Flight " + flightNumber + "edited succesfully");

    return "redirect:/admin/flights";
  }

  @GetMapping("/admin/flights/new")
  public String getAddPage(
      Model model) {
    CreateFlightRequest form = new CreateFlightRequest();
    model.addAttribute("form", form);
    return "admin/add";
  }

  @PostMapping("/admin/flights/new")
  public String addFlight(
      @Valid @ModelAttribute("form") CreateFlightRequest filledForm,
      BindingResult result,
      RedirectAttributes redirect) {

    if (result.hasErrors()) {
      return "admin/add";
    }
    flightService.createFlight(filledForm);
    redirect.addFlashAttribute("success",
        "Flight " + filledForm.getFlightNumber() + " created successfully");
    return "redirect:/admin/flights";
  }

}
