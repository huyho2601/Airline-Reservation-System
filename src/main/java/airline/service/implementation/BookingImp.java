package airline.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import airline.dto.CreateBookingRequest;
import airline.entity.Booking;
import airline.entity.Seat;
import airline.entity.User;
import airline.entity.enums.BookingStatus;
import airline.entity.enums.SeatStatus;
import airline.entity.enums.UserRole;
import airline.error.ResourceNotFoundException;
import airline.error.SeatUnavailableException;
import airline.repository.BookingRepository;
import airline.repository.SeatRepository;
import airline.repository.UserRepository;
import airline.service.BookingService;

@Service 
public class BookingImp implements BookingService {

  private String USERNOTFOUND = "User not found: ";

  private final BookingRepository bookingRepository;
  private final SeatRepository seatRepository;
  private final UserRepository userRepository;

  public BookingImp(BookingRepository bookingRepository, SeatRepository seatRepository, UserRepository userRepository) {
    this.bookingRepository = bookingRepository;
    this.seatRepository = seatRepository;
    this.userRepository = userRepository;
  }

  @Override
  public List<Booking> getAllBookings(long adminId) {

    // Authenticate admin
    User user = userRepository.findById(adminId).
        orElseThrow(() -> new ResourceNotFoundException("User not found: " + adminId));
      
      if (user.getRole() != UserRole.ADMIN) {
      throw new AccessDeniedException("Not admin");
    }

    return bookingRepository.findAll();
  }

  @Override
  public Booking getBooking(long id, User user) {

    // Authenticate user
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

    if (booking.getUser().getId() != user.getId()) {
      throw new AccessDeniedException("Not your booking");
    }

    return booking;
  }
  
  // 1: User should not be able to send id himself, this should come from the Spring Security (authentication concern)
  // 2: Seat should not be sent as object. Retrieve seat by its id
  // 3: Booking time should be .now()
  // 4: When creating the booking, status should be AVAILABLE
  // Stay alet with race condition: when 2 or more clients book a same seat at a same time

  @Override
  public Booking createBooking(CreateBookingRequest request, User user) {
     
    // 2 - Retrieve seat by id
    Seat seat = seatRepository.findById(request.getSeatId())
        .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + request.getSeatId()));

    // 4
    if (seat.getStatus() != SeatStatus.AVAILABLE) {
      throw new SeatUnavailableException("Seat is not available: " + seat.getId());
    }

    Booking booking = new Booking(user, LocalDateTime.now(), seat, BookingStatus.CONFIRMED);

    seat.setStatus(SeatStatus.BOOKED); // Set seat to booked

    return bookingRepository.save(booking);
  }

  @Override
  public Booking updateSeatBooking(long bookingId, long seatId, User currentUser) {

    // Retrieve existing booking and seat
    Booking existingBooking = bookingRepository.findById(bookingId)
    .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    Seat existingSeat = existingBooking.getSeat();

    // Authenticate user
    if (existingBooking.getUser().getId() != currentUser.getId() && currentUser.getRole() != UserRole.ADMIN) {
      throw new AccessDeniedException("Not your booking");
    }

    // Retrieve new seat and check for availability
    Seat newSeat = seatRepository.findById(seatId)
        .orElseThrow(() -> new ResourceNotFoundException("Seat not found: " + seatId));

    if (newSeat.getStatus() == SeatStatus.AVAILABLE) {
      existingBooking.setSeat(newSeat);
      newSeat.setStatus(SeatStatus.BOOKED);
      existingSeat.setStatus(SeatStatus.AVAILABLE);
    } else {
      throw new SeatUnavailableException("Seat is not available: " + newSeat.getId());
    }

    return bookingRepository.save(existingBooking);
  }

  // Cancel booking
  @Override
  public void deleteBooking(long id, User currentUser) {
    
    // Retrieve the booking
    Booking booking = getBooking(id, currentUser);

    // Release the seat
    Seat seat = booking.getSeat();
    seat.setStatus(SeatStatus.AVAILABLE);

    bookingRepository.delete(booking);
  }

}
