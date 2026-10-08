package airline.service;

import airline.dto.UpdateFlightRequest;
import airline.dto.CreateFlightRequest;
import airline.dto.SearchFlightRequest;
import airline.entity.Flight;
import java.util.List;

public interface FlightService {
  List<Flight> getAllFlights();

  Flight getFlightByFlightNumber(String flightNumber);

  List<Flight> searchFlight(SearchFlightRequest searchRequest);

  Flight createFlight(CreateFlightRequest flight);
  
  Flight updateFlight(String flightNumber, UpdateFlightRequest request);

  void deleteFlight(String flightNumber);
}
