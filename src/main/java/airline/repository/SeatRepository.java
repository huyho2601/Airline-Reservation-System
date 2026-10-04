package airline.repository;

import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.enums.SeatStatus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
  List<Seat> findByFlight(Flight flight);

  List<Seat> findByFlightAndStatus(Flight flight, SeatStatus status);

  Optional<Seat> findByFlightAndSeatNumber(Flight flight, String seatNumber);
}