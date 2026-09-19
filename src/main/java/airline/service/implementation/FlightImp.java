package airline.service.implementation;

import airline.repository.FlightRepsitory;
import airline.service.FlightService;

import java.util.List;

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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createFlight'");
  }

  @Override
  public Flight updateFlight(Long id, Flight flight) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateFlight'");
  }

  @Override
  public Flight deleteFlight(Long id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteFlight'");
  }

  
  
}
