package airline.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import airline.dto.CreateBookingRequest;
import airline.entity.Booking;
import airline.entity.User;
import airline.repository.BookingRepository;
import airline.security.CustomUserDetails;
import airline.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/bookings")
public class BookingController {
  
  private final BookingService bookingService;
 
  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;

  }

  @GetMapping
  public List<Booking> getAllBookings() {
    return bookingService.getAllBookings();
  }   

  @GetMapping("/{bookingId}")
  public Booking getBookingById(
    @PathVariable long bookingId,
    @AuthenticationPrincipal CustomUserDetails principal) {
    User currentUser = principal.getUser();
    return bookingService.getBooking(bookingId, currentUser);
  }
  
  @GetMapping("/me")
  public List<Booking> getBookingForUser(
    @AuthenticationPrincipal CustomUserDetails principal) {
      
    List<Booking> allBooking = bookingService.getBookingsForUser(principal.getUser());

    return allBooking;
  }
  
  
  @PostMapping
  public ResponseEntity<Booking> createBooking(
    @Valid @RequestBody CreateBookingRequest booking, 
    @AuthenticationPrincipal CustomUserDetails principal) {

    User currentUser = principal.getUser();

    Booking newBooking = bookingService.createBooking(booking, currentUser);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{bookingId}")
        .buildAndExpand(newBooking.getId())
        .toUri();

    return ResponseEntity.created(location).body(newBooking);
  }

  @PutMapping("/{bookingId}/seat")
  public Booking changeSeat(
      @PathVariable long bookingId,
      @RequestBody String seatNumber,
      @AuthenticationPrincipal CustomUserDetails principal) {
      Booking updateBooking = bookingService.updateSeatBooking(bookingId, seatNumber, principal.getUser());
    return updateBooking;
  }

  @DeleteMapping("/{bookingId}")
  public ResponseEntity<Void> cancelBooking(
      @PathVariable long bookingId,
     @AuthenticationPrincipal CustomUserDetails principal) {
    bookingService.deleteBooking(bookingId, principal.getUser());
    return ResponseEntity.noContent().build();
  }
  
}
