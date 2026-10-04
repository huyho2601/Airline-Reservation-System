package airline.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import airline.dto.CreateBookingRequest;
import airline.entity.Booking;
import airline.entity.User;
import airline.entity.enums.UserRole;
import airline.error.ResourceNotFoundException;
import airline.error.UnauthorizedUserException;
import airline.repository.UserRepository;
import airline.service.BookingService;
import airline.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
  
  private BookingService bookingService;
  private UserRepository userRepository;

  public BookingController(BookingService bookingService, UserRepository userRepository) {
    this.bookingService = bookingService;
    this.userRepository = userRepository;
  }

  // Temporary solution
  private User resolveUser(long userId) {

    User currentUser = userRepository.findById(userId).
      orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    
    return currentUser;
  }

  // GET API -> Fetch all flights
  @GetMapping
  public List<Booking> getAllBookings(@RequestHeader ("X-User-Id")  long userId) {

    User user = resolveUser(userId);

    if (user.getRole() != UserRole.ADMIN) {
      throw new UnauthorizedUserException("Access denied!");
    }

    return bookingService.getAllBookings();
  }

  // TODO: implement @AuthenticationPrincipal later (@AuthenticationPrincipal User urrentUser)
  @GetMapping("/{bookingId}")
  public Booking getBookingById(
    @PathVariable long bookingId,
    @RequestHeader ("X-User-Id")  Long userId) {

    User currentUser = resolveUser(userId);
    
    return bookingService.getBooking(bookingId, currentUser);
        
  }
  
  // TODO: Implement @AuthenticationPrincipal later
  @PostMapping
  public ResponseEntity<Booking> createBooking(
    @Valid @RequestBody CreateBookingRequest booking, 
      @RequestHeader ("X-User-Id")  Long userId) {

    // Temporary solution    
    User currentUser = resolveUser(userId);
    
    Booking newBooking = bookingService.createBooking(booking, currentUser);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{bookingId}")
        .buildAndExpand(newBooking.getId())
        .toUri();

    return ResponseEntity.created(location).body(newBooking);
  }
}
