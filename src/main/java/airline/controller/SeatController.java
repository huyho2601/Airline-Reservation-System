package airline.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import airline.entity.Seat;
import airline.service.SeatService;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

  private final SeatService seatService;

  public SeatController(SeatService seatService) {
    this.seatService = seatService;
  }

  @GetMapping
  public List<Seat> getAvailableSeats(@RequestParam String flightNumber) {
    return seatService.getAvailableSeats(flightNumber);
  }

  
  
  
}
