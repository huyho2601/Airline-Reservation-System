package airline.service.implementation;

import airline.repository.FlightRepsitory;
import airline.service.FlightService;

import java.util.List;

import airline.dto.UpdateFlightRequest;
import airline.entity.Flight;

public class FlightImp implements FlightService {

  private FlightRepsitory flightRepsitory;

  @Override
  public List<Flight> getAllFlights() {
    return (List<Flight>) flightRepsitory.findAll();
  }

  @Override
  public Flight getFlightByFlightNumber(String flightNumber) {
    Flight flight = flightRepsitory.findByFlightNumber(flightNumber)
        .orElseThrow(() -> new RuntimeException("Flight not found: " + flightNumber));
    return flight;
  }

  @Override
  public Flight createFlight(Flight flight) {
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
