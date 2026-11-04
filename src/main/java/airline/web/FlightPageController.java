package airline.web;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import airline.entity.Flight;
import airline.entity.Seat;
import airline.service.FlightService;
import airline.service.SeatService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller 
public class FlightPageController {

  private final FlightService flightService;
  private final SeatService seatService;

  public FlightPageController(FlightService flightService, SeatService seatService) {
    this.flightService = flightService;
    this.seatService = seatService;
  }

  @GetMapping({"/","/flights"})
  public String search(
    @RequestParam (required = false) String origin,
    @RequestParam (required = false) String destination,
    @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
    @RequestParam (required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
      Model model) {

    model.addAttribute("origin", origin);
    model.addAttribute("destination", destination);
    model.addAttribute("fromDate", fromDate);
    model.addAttribute("toDate", toDate);

    if (!StringUtils.hasText(origin) || !StringUtils.hasText(destination) || fromDate == null || toDate == null) {
      return "flights/search";
    }
    origin = origin.trim();
    destination = destination.trim();

    if (toDate.isBefore(fromDate)) {
      model.addAttribute("error", "to dates can't be before from date");
      return "flights/search";
    }

    List<Flight> flights = flightService.searchFlight(origin, destination, fromDate, toDate);
    model.addAttribute("flights", flights);

    return "flights/search";
  }
  
  @GetMapping("/flights/{flightNumber}")
  public String flightDetails(
      @PathVariable String flightNumber,
        Model model,
         RedirectAttributes redirectAttributes ) {

    TreeMap<Integer, ArrayList<Seat>> seatMap = new TreeMap<>();
    
    Flight flight = flightService.getFlightByFlightNumber(flightNumber);

    if (flight == null) {
      redirectAttributes.addFlashAttribute("error", "Flight not found");
      return "redirect:/flights";
    }

    List<Seat> allSeats = seatService.getAllSeats(flightNumber);

    for (Seat seat : allSeats) {
      String rowString = seat.getSeatNumber().replaceAll("[^0-9]", "");
      int rowInt = Integer.parseInt(rowString);
      seatMap.computeIfAbsent(rowInt, k -> new ArrayList<>()).add(seat);
    }

    model.addAttribute("flight", flight);
    model.addAttribute("seatMap", seatMap);

    return "flights/details";
  }
  
  
}
