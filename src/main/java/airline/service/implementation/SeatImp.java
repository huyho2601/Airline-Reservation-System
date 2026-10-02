package airline.service.implementation;

import java.util.List;

import airline.dto.CreateSeatRequest;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.enums.SeatStatus;
import airline.error.ResourceNotFoundException;
import airline.repository.SeatRepository;
import airline.repository.FlightRepository;
import airline.service.SeatService;

public class SeatImp implements SeatService {

  private final SeatRepository seatRepository;
  private final FlightRepository flightRepository;

  public SeatImp(SeatRepository seatRepository, FlightRepository flightRepository) {
    this.seatRepository = seatRepository;
    this.flightRepository = flightRepository;
  }

  @Override
  public List<Seat> getAllSeats(Long flightId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAllSeats'");
  }

  @Override
  public List<Seat> getAvailableSeats(Long flightId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAvailableSeats'");
  }

  @Override
  public Seat getSeatByIdAndFlightId(Long id, Long flightId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getSeatByIdAndFlightId'");
  }

  // @Override
  // public Seat createSeat(CreateSeatRequest createRequest, Long flightId) {

  //   Flight flight = flightRepository.findById(flightId)
  //       .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightId));
    
  //   Seat newSeat = new Seat(
  //     createRequest.seatNumber(),
  //     SeatStatus.AVAILABLE,
  //         flight
  //   );

  //   return seatRepository.save(newSeat);
  // }

  @Override
  public Seat updateSeat(Seat seat, Long flightId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'updateSeat'");
  }

  @Override
  public Seat reserveSeat(Long seatId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'reserveSeat'");
  }

  @Override
  public Seat cancelSeatReservation(Long seatId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'cancelSeatReservation'");
  }

  @Override
  public void deleteSeat(Long id, Long flightId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteSeat'");
  }

}
