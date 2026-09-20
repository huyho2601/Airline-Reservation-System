package airline.service;

import airline.dto.UpdateFlightRequest;
import airline.entity.Flight;
import java.util.List;

public interface FlightService {
  List<Flight> getAllFlights();

  Flight getFlightByFlightNumber(String flightNumber);

  Flight createFlight(Flight flight);
  
  Flight updateFlight(String flightNumber, UpdateFlightRequest request);

  void deleteFlight(String flightNumber);
}
