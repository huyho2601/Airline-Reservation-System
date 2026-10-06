package airline.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import airline.entity.Seat;
import airline.service.SeatService;

@RestController
@RequestMapping("/api/flights/{flightNumber}/seats")
public class SeatController {

  private final SeatService seatService;

  public SeatController(SeatService seatService) {
    this.seatService = seatService;
  }

  // GET /api/flights/KL1234/seats
  // GET /api/flights/KL1234/seats?available=true
  @GetMapping
  public List<Seat> getSeats(
      @PathVariable String flightNumber,
      @RequestParam(defaultValue = "false") boolean available) {
    return available
        ? seatService.getAvailableSeats(flightNumber)
        : seatService.getAllSeats(flightNumber);
  }

  // GET /api/flights/KL1234/seats/12A
  @GetMapping("/{seatNumber}")
  public Seat getSeat(
    @PathVariable String flightNumber,
        @PathVariable String seatNumber) {
    return seatService.getSeat(flightNumber, seatNumber);
  }

  // // POST /api/flights/KL1234/seats/12A/reservation
  // @PostMapping("/{seatNumber}/reservation")
  // public Seat reserveSeat(
  //   @PathVariable String flightNumber,
  //       @PathVariable String seatNumber) {
  //   return seatService.reserveSeat(flightNumber, seatNumber);
  // }

  // // DELETE /api/flights/KL1234/seats/12A/reservation
  // @DeleteMapping("/{seatNumber}/reservation")
  // public Seat cancelReservation(
  //   @PathVariable String flightNumber,
  //       @PathVariable String seatNumber) {
  //   return seatService.cancelSeatReservation(flightNumber, seatNumber);
  // }

  // DELETE /api/flights/KL1234/seats/12A
  @DeleteMapping("/{seatNumber}")
  public ResponseEntity<Void> deleteSeat(
    @PathVariable String flightNumber,
        @PathVariable String seatNumber) {
    seatService.deleteSeat(flightNumber, seatNumber);
    return ResponseEntity.noContent().build();
  }
}