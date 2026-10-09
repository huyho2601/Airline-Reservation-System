package airline.service.implementation;

import airline.repository.FlightRepository;
import airline.repository.SeatRepository;
import airline.service.FlightService;
import airline.error.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import airline.dto.UpdateFlightRequest;
import airline.dto.CreateFlightRequest;
import airline.dto.SearchFlightRequest;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.enums.SeatStatus;

@Service
public class FlightServiceImp implements FlightService {

  private final FlightRepository flightRepository;
  private final SeatRepository seatRepository;

  // Constructor
  public FlightServiceImp(FlightRepository flightRepository, SeatRepository seatRepository) {
    this.flightRepository = flightRepository;
    this.seatRepository = seatRepository;
  }

  // Main lofic
  @Override
  public List<Flight> getAllFlights() {
    return (List<Flight>) flightRepository.findAll();
  }

  @Override
  public Flight getFlightByFlightNumber(String flightNumber) {
    Flight flight = flightRepository.findByFlightNumber(flightNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightNumber));
    return flight;
  }

  @Override
  @Transactional
  public Flight createFlight(CreateFlightRequest createRequest) {

    // Check for duplicate
    if (flightRepository.existsByFlightNumber(createRequest.getFlightNumber())) {
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

    Flight savedFlight = flightRepository.save(newFlight);

    // Populate seats for the flight
    List<Seat> seatList = populateSeatsForFlight(newFlight.getTotalSeats(), savedFlight);

    seatRepository.saveAll(seatList);
    return savedFlight;
  }

  private List<Seat> populateSeatsForFlight(int totalSeats, Flight flight) {
    List<Seat> seatList = new ArrayList<>();
    String[] letters = { "A", "B", "C", "D", "E", "F" };
    int col = 0;
    int row = 1;
    for (int i = 1; i <= totalSeats; i++) {
      if (col >= letters.length) {
        col = 0;
        row++;
      }
      String seatNumber = row + letters[col];
      Seat seat = new Seat(seatNumber, SeatStatus.AVAILABLE, flight);
      col++;
      seatList.add(seat);
    }
    return seatList;
  }

  @Override
  @Transactional
  public Flight updateFlight(String flightNumber, UpdateFlightRequest request) {

    // Check if the flight already existed
    Flight existingFlight = getFlightByFlightNumber(flightNumber);

    existingFlight.setArrivalTime(request.getArrivalTime());
    existingFlight.setDepartureTime(request.getDepartureTime());
    existingFlight.setOrigin(request.getOrigin());
    existingFlight.setDestination(request.getDestination());
    existingFlight.setPrice(request.getPrice());

    return flightRepository.save(existingFlight);
  }

  @Override
  @Transactional
  public void deleteFlight(String flightNumber) {
    Flight flight = getFlightByFlightNumber(flightNumber);

    flightRepository.delete(flight);
  }

  @Override
  public List<Flight> searchFlight(SearchFlightRequest searchRequest) {
    return flightRepository.findByOriginIgnoreCaseAndDestinationIgnoreCaseAndDepartureTimeBetween(
        searchRequest.origin(),
        searchRequest.destination(),
        searchRequest.start(),
        searchRequest.end());
  }
}
