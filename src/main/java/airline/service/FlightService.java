package airline.service;

import airline.entity.Flight;
import java.util.List;

public interface FlightService {
  List<Flight> getAllFlights();

  Flight getFlightById(Long id);

  Flight createFlight(Flight flight);
  
  Flight updateFlight(Long id, Flight flight);

  Flight deleteFlight(Long id);
}
