package airline.service;

import airline.entity.Seat;
import java.util.List;

public interface SeatService {
  List<Seat> getAllSeats(String flightNumber);

  List<Seat> getAvailableSeats(String flightNumber);

  Seat getSeat(String flightNumber, String seatNumber);

  Seat reserveSeat(String flightNumber, String seatNumber);

  Seat cancelSeatReservation(String flightNumber, String seatNumber);

  void deleteSeat(String flightNumber, String seatNumber);
}