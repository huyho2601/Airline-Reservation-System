package airline.controller;

import airline.entity.Flight;
import airline.entity.User;
import airline.entity.enums.UserRole;
import airline.error.ResourceNotFoundException;
import airline.error.UnauthorizedUserException;
import airline.repository.UserRepository;
import airline.service.FlightService;
import airline.dto.CreateFlightRequest;
import airline.dto.SearchFlightRequest;
import airline.dto.UpdateFlightRequest;
import jakarta.validation.Valid;


import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/flights")
public class FlightController {

  private FlightService flightService;
  private UserRepository userRepository;

  // Constructor
  public FlightController(FlightService flightService, UserRepository userRepository) {
    this.flightService = flightService;
    this.userRepository = userRepository;
  }

  // Temporary solution to resolve user by ID
  private User resolveUser(long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    return user;
  }
  
  // Temporary solution to check if the user is an admin
  private void checkAdmin(User user) {
    if (user.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedUserException("Access denied!");
    }
  }

  // GET API -> Fetch all flights
  @GetMapping
  public List<Flight> getAllFlights(@RequestHeader ("X-User-Id")  long userId) {

    User user = resolveUser(userId);
    checkAdmin(user);

    return flightService.getAllFlights();
  }

  @GetMapping("/{flightNumber}")
  public Flight getFlightByFlightNumber(@PathVariable String flightNumber) {
    return flightService.getFlightByFlightNumber(flightNumber);
  }
  
  @GetMapping("/search")
  public List<Flight> searchForFlights(
    @RequestParam String origin,
      @RequestParam String destination,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate date) {
    LocalDateTime departureDateStart = date.atStartOfDay();
    LocalDateTime departureDateEnd = date.atTime(23, 59, 59);
    SearchFlightRequest searchRequest = new SearchFlightRequest(origin, destination, departureDateStart, departureDateEnd);
      return flightService.searchFlight(searchRequest);
  }
  

  @PostMapping
  public ResponseEntity<Flight> createFlight(
    @Valid @RequestBody CreateFlightRequest flight,
      @RequestHeader("X-User-Id") long userId) {
        
    User user = resolveUser(userId);
    checkAdmin(user);

    Flight newFlight = flightService.createFlight(flight);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{flightNumber}")
        .buildAndExpand(newFlight.getFlightNumber())
        .toUri();

    return ResponseEntity.created(location).body(newFlight);
  }

  @PutMapping("/{flightNumber}")
  public ResponseEntity<Flight> updateFlight(@PathVariable String flightNumber,
      @RequestHeader("X-User-Id") long userId,
      @RequestBody UpdateFlightRequest newRequest) {

    User user = resolveUser(userId);
    checkAdmin(user);

    Flight updatedFlight = flightService.updateFlight(flightNumber, newRequest);

    return new ResponseEntity<>(updatedFlight, HttpStatus.OK);
  }
  
  @DeleteMapping ("/{flightNumber}")
  public ResponseEntity<String> deleteFlight(
    @PathVariable String flightNumber,
      @RequestHeader("X-User-Id") long userId) {

    User user = resolveUser(userId);
    checkAdmin(user);

    flightService.deleteFlight(flightNumber);
    
    return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
  }

}
