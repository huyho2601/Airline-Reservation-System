package airline.controller;

import airline.entity.Flight;
import airline.service.FlightService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/flight_service")
public class FlightController {

  private FlightService flightService;

  // Constructor
  public FlightController(FlightService flightService) {
    this.flightService = flightService;
  }

  // GET API -> Fetch all flights
  @GetMapping("/allflights")
  public List<Flight> getAllFlights() {
    return flightService.getAllFlights();
  }

  @GetMapping("/{flightNumber}")
  public Flight getFlightByFlightNumber(@PathVariable String flightNumber) {
    return flightService.getFlightByFlightNumber(flightNumber);
  }

  @PostMapping
  public ResponseEntity<Flight> createFlight(@Valid @RequestBody Flight flight) {

    Flight newFlight = flightService.createFlight(flight);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{flightNumber}")
        .buildAndExpand(newFlight.getFlightNumber())
        .toUri();

    return ResponseEntity.created(location).body(newFlight);

  }

}
