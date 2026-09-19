package airline.service;

import airline.entity.Seat;
import java.util.List;

public interface SeatService {
  List<Seat> getAllSeats(Long flightId);
  List<Seat> getAvailableSeats(Long flightId);

  Seat getSeatByIdAndFlightId(Long id, Long flightId);
  
  Seat createSeat(Seat seat, Long flightId);

  Seat updateSeat(Seat seat, Long flightId);
  
  Seat reserveSeat(Long seatId);
  Seat cancelSeatReservation(Long seatId);

  void deleteSeat(Long id, Long flightId);
}
