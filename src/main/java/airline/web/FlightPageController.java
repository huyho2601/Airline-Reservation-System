package airline.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import airline.entity.Flight;
import airline.service.FlightService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller 
public class FlightPageController {

  private final FlightService flightService;

  public FlightPageController(FlightService flightService) {
    this.flightService = flightService;
  }

  @GetMapping({"/","flights"})
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
  
  
}
