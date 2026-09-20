package airline.repository;

import airline.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepsitory extends JpaRepository<Flight, Long> {
  Optional<Flight> findByFlightNumber(String flightNumber);

  
}
