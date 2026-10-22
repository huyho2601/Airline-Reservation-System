package airline.service;

import airline.dto.UpdateFlightRequest;
import airline.dto.CreateFlightRequest;
import airline.dto.SearchFlightRequest;
import airline.entity.Flight;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
  List<Flight> getAllFlights();

  Flight getFlightByFlightNumber(String flightNumber);

  List<Flight> searchFlight(String origin, String destination, LocalDate from, LocalDate to);

  Flight createFlight(CreateFlightRequest flight);
  
  Flight updateFlight(String flightNumber, UpdateFlightRequest request);

  void deleteFlight(String flightNumber);
}
