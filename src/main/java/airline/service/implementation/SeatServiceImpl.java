package airline.service.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.enums.SeatStatus;
import airline.error.ResourceNotFoundException;
import airline.error.SeatUnavailableException;
import airline.repository.FlightRepository;
import airline.repository.SeatRepository;
import airline.service.SeatService;

@Service
@Transactional(readOnly = true)
public class SeatServiceImpl implements SeatService {

  private final SeatRepository seatRepository;
  private final FlightRepository flightRepository;

  public SeatServiceImpl(SeatRepository seatRepository, FlightRepository flightRepository) {
    this.seatRepository = seatRepository;
    this.flightRepository = flightRepository;
  }

  // Reads

  @Override
  public List<Seat> getAllSeats(String flightNumber) {
    return seatRepository.findByFlight(findFlight(flightNumber));
  }

  @Override
  public List<Seat> getAvailableSeats(String flightNumber) {
    return seatRepository.findByFlightAndStatus(findFlight(flightNumber), SeatStatus.AVAILABLE);
  }

  @Override
  public Seat getSeat(String flightNumber, String seatNumber) {
    return findSeat(flightNumber, seatNumber);
  }

  // Writes 
  
  @Override
  @Transactional
  public Seat reserveSeat(String flightNumber, String seatNumber) {
    Seat seat = findSeat(flightNumber, seatNumber);

    if (seat.getStatus() != SeatStatus.AVAILABLE) {
      throw new SeatUnavailableException(
          "Seat " + seatNumber + " on flight " + flightNumber + " is not available");
    }

    seat.setStatus(SeatStatus.RESERVED);
    return seat;
  }

  @Override
  @Transactional
  public Seat cancelSeatReservation(String flightNumber, String seatNumber) {
    Seat seat = findSeat(flightNumber, seatNumber);

    if (seat.getStatus() != SeatStatus.RESERVED) {
      throw new SeatUnavailableException(
          "Seat " + seatNumber + " on flight " + flightNumber + " is not reserved");
    }

    seat.setStatus(SeatStatus.AVAILABLE);
    return seat;
  }

  @Override
  @Transactional
  public void deleteSeat(String flightNumber, String seatNumber) {
    seatRepository.delete(findSeat(flightNumber, seatNumber));
  }

  // Helpers

  private Flight findFlight(String flightNumber) {
    return flightRepository.findByFlightNumber(flightNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightNumber));
  }

  private Seat findSeat(String flightNumber, String seatNumber) {
    Flight flight = findFlight(flightNumber);
    return seatRepository.findByFlightAndSeatNumber(flight, seatNumber)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Seat " + seatNumber + " not found on flight " + flightNumber));
  }
}