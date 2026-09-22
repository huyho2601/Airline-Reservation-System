package airline.service.implementation;

import airline.repository.FlightRepsitory;
import airline.service.FlightService;
import airline.error.*;

import java.util.List;

import org.springframework.stereotype.Service;

import airline.dto.UpdateFlightRequest;
import airline.dto.CreateFlightRequest;
import airline.entity.Flight;

@Service 
public class FlightImp implements FlightService {

  private final FlightRepsitory flightRepsitory;

  // Constructor
  public FlightImp(FlightRepsitory flightRepsitory) {
    this.flightRepsitory = flightRepsitory;
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
  public Flight createFlight(CreateFlightRequest createRequest) {

    // Check for duplicate
    if (flightRepsitory.existsByFlightNumber(createRequest.getFlightNumber())) {
      throw new DuplicateResourceException(
          "Flight already exists: " + createRequest.getFlightNumber());
    }
    
    // Create new flight instance
    Flight newFlight = new Flight();
    newFlight.setFlightNumber(createRequest.getFlightNumber());
    newFlight.setArrivalTime(createRequest.getArrivalTime());
    newFlight.setDepartureTime(createRequest.getDepartureTime());
    newFlight.setOrigin(createRequest.getOrigin());
    newFlight.setDestination(createRequest.getDestination());
    newFlight.setPrice(createRequest.getPrice());
    newFlight.setTotalSeats(createRequest.getTotalSeats());

    return flightRepsitory.save(newFlight);
  }

  @Override
  public Flight updateFlight(String flightNumber, UpdateFlightRequest request) {

    // Check if the flight already existed
    Flight existingFlight = flightRepsitory.findByFlightNumber(flightNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightNumber));

    existingFlight.setArrivalTime(request.getArrivalTime());
    existingFlight.setDepartureTime(request.getDepartureTime());
    existingFlight.setOrigin(request.getOrigin());
    existingFlight.setDestination(request.getDestination());
    existingFlight.setPrice(request.getPrice());

    return flightRepsitory.save(existingFlight);
  }

  @Override
  public void deleteFlight(String flightNumber) {
    Flight flight = getFlightByFlightNumber(flightNumber);

    flightRepsitory.delete(flight);
  }
}
