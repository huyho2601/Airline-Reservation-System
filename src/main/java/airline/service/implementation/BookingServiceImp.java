package airline.service.implementation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import airline.dto.CreateBookingRequest;
import airline.entity.Booking;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.User;
import airline.entity.enums.BookingStatus;
import airline.entity.enums.SeatStatus;
import airline.entity.enums.UserRole;
import airline.error.ResourceNotFoundException;
import airline.error.SeatUnavailableException;
import airline.repository.BookingRepository;
import airline.repository.FlightRepository;
import airline.repository.SeatRepository;
import airline.service.BookingService;

@Service
public class BookingServiceImp implements BookingService {

  private final BookingRepository bookingRepository;
  private final SeatRepository seatRepository;
  private final FlightRepository flightRepository;
 
  public BookingServiceImp(BookingRepository bookingRepository, SeatRepository seatRepository,
      FlightRepository flightRepository) {
    this.bookingRepository = bookingRepository;
    this.seatRepository = seatRepository;
    this.flightRepository = flightRepository;
  }

  // TODO: @AuthenticationPrincipal
  @Override
  public List<Booking> getAllBookings() {
    return bookingRepository.findAll();
  }

  @Override
  public Booking getBooking(long id, User user) {
    // Authenticate user
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));

    checkOwnerOrAdmin(booking, user);

    return booking;
  }

  // 1: User should not be able to send id himself, this should come from the
  // Spring Security (authentication concern)
  // 2: Seat should not be sent as object. Retrieve seat by its id
  // 3: Booking time should be .now()
  // 4: When creating the booking, status should be AVAILABLE
  // Stay alet with race condition: when 2 or more clients book a same seat at a
  // same time

  @Transactional
  @Override
  public Booking createBooking(CreateBookingRequest request, User user) {

    // 2 - Retrieve seat by seat number and flight number
    Flight flight = findFlight(request.flightNumber());
    Seat seat = findSeat(flight, request.seatNumber());

    // Validate flight and seat existence
    if (flight == null) {
      throw new ResourceNotFoundException("Flight not found: " + request.flightNumber());
    }

    if (seat == null) {
      throw new ResourceNotFoundException("Seat not found: " + request.seatNumber());
    }

    // Validate flight availability
    validateFlight(flight);

    // 4: Validate seat availability
    if (seat.getStatus() != SeatStatus.AVAILABLE) {
      throw new SeatUnavailableException("Seat is not available: " + seat.getId());
    }

    seat.setStatus(SeatStatus.BOOKED); // Set seat to booked
    seatRepository.save(seat);

    Booking booking = new Booking(user, LocalDateTime.now(), seat, BookingStatus.CONFIRMED);

    return bookingRepository.save(booking);
  }

  @Transactional
  @Override
  public Booking updateSeatBooking(long bookingId, String seatNumber, User currentUser) {

    // Retrieve existing booking and seat
    Booking existingBooking = getBooking(bookingId, currentUser);
    Seat oldSeat = existingBooking.getSeat();

    // Authenticate user
    checkOwnerOrAdmin(existingBooking, currentUser);

    // Retrieve new seat and check for availability
    Flight flight = oldSeat.getFlight();

    // Validate flight availability
    validateFlight(flight);

    Seat newSeat = findSeat(flight, seatNumber);

    if (newSeat.getStatus() == SeatStatus.AVAILABLE) {
      existingBooking.setSeat(newSeat);
      newSeat.setStatus(SeatStatus.BOOKED);
      oldSeat.setStatus(SeatStatus.AVAILABLE);
    } else {
      throw new SeatUnavailableException("Seat is not available: " + newSeat.getSeatNumber());
    }

    return bookingRepository.save(existingBooking);
  }

  // Cancel booking
  @Override
  @Transactional
  public void deleteBooking(long id, User currentUser) {

    // Retrieve the booking
    Booking booking = getBooking(id, currentUser);

    // Release the seat
    Seat seat = booking.getSeat();
    seat.setStatus(SeatStatus.AVAILABLE);

    bookingRepository.delete(booking);
  }

  // Helper

  private Flight findFlight(String flightNumber) {
    return flightRepository.findByFlightNumber(flightNumber)
        .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightNumber));
  }

  private Seat findSeat(Flight flight, String seatNumber) {
    Seat seat = seatRepository.findByFlightAndSeatNumber(flight, seatNumber)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Seat not found for flight: " + flight.getFlightNumber() + ", seat number: " + seatNumber));
    return seat;  
  }

  private void checkOwnerOrAdmin(Booking booking, User currentUser) {
    boolean isOwner = booking.getUser().getId() == currentUser.getId();

    if (!isOwner && currentUser.getRole() != UserRole.ADMIN) {
      throw new AccessDeniedException("Not your booking");
    }
  }
  
  private void validateFlight(Flight flight) {
    if (flight.getDepartureTime().isBefore(LocalDateTime.now())) {
      throw new ResourceNotFoundException("Flight has already departed: " + flight.getFlightNumber());
    }
  }

}
