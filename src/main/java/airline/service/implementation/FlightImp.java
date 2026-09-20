package airline.service.implementation;

import airline.repository.FlightRepsitory;
import airline.service.FlightService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import airline.dto.UpdateFlightRequest;
import airline.entity.Flight;

@Service 
public class FlightImp implements FlightService {

  private final FlightRepsitory flightRepsitory;

  // Constructor
  public FlightImp(FlightRepsitory flightRepsitory) {
    this.flightRepsitory = flightRepsitory;
  }

  // Reponses

  // Not found
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) {
      super(msg);
    }
  }

  // Existing entity
  @ResponseStatus(HttpStatus.CONFLICT)
  public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
  }

  
  // Main lofic
  @Override
  public List<Flight> getAllFlights() {
    return (List<Flight>) flightRepsitory.findAll();
  }

  @Override
  public Flight getFlightByFlightNumber(String flightNumber) {
    Flight flight = flightRepsitory.findByFlightNumber(flightNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightNumber));
    return flight;
  }

  @Override
  public Flight createFlight(Flight flight) {

    // Check for duplicate
    if (flightRepsitory.existbyFlightNumber(flight.getFlightNumber())) {
      throw new DuplicateResourceException(
                "Flight already exists: " + flight.getFlightNumber());
    }

    return flightRepsitory.save(flight);
  }

  @Override
  public Flight updateFlight(String flightNumber, UpdateFlightRequest request) {
    Flight existingFlight = flightRepsitory.findByFlightNumber(flightNumber)
        .orElseThrow(() -> new RuntimeException("Flight does not exist: " + flightNumber));

    existingFlight.setArrivalTime(request.getArrivalTime());
    existingFlight.setDepartureTime(request.getDepartureTime());
    existingFlight.setOrigin(request.getOrigin());
    existingFlight.setDestination(request.getDestination());
    existingFlight.setPrice(request.getPrice());

    return existingFlight;
  }

  @Override
  public void deleteFlight(String flightNumber) {
    Flight flight = getFlightByFlightNumber(null);
    flightRepsitory.delete(flight);
  }
}
