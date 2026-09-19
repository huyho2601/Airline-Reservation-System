package airline.repository;

import airline.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepsitory extends JpaRepository<Flight, Long> {
  
}
