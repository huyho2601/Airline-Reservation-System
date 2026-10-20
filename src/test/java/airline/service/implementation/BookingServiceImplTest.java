package airline.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import airline.dto.CreateBookingRequest;
import airline.entity.Booking;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.User;
import airline.entity.enums.BookingStatus;
import airline.entity.enums.SeatStatus;
import airline.entity.enums.UserRole;
import airline.error.ConflictDataInputException;
import airline.error.ResourceNotFoundException;
import airline.error.SeatUnavailableException;
import airline.repository.BookingRepository;
import airline.repository.FlightRepository;
import airline.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

  @Mock BookingRepository bookingRepository;
  @Mock SeatRepository seatRepository;
  @Mock FlightRepository flightRepository;

  @InjectMocks BookingServiceImpl bookingService;

  private User owner;
  private User stranger;
  private User admin;
  private Flight futureFlight;
  private Seat seat;

  @BeforeEach
  void setUp() {
    owner = user(1L, UserRole.CLIENT);
    stranger = user(2L, UserRole.CLIENT);
    admin = user(3L, UserRole.ADMIN);

    futureFlight = new Flight();
    futureFlight.setFlightNumber("KL123");
    futureFlight.setDepartureTime(LocalDateTime.now().plusDays(1));

    seat = new Seat("1A", SeatStatus.AVAILABLE, futureFlight);
  }

  // ---------- createBooking ----------

  @Test
  void createBooking_availableSeat_booksSeatAndSavesBooking() {
    // arrange
    when(flightRepository.findByFlightNumber("KL123")).thenReturn(Optional.of(futureFlight));
    when(seatRepository.findByFlightAndSeatNumber(futureFlight, "1A")).thenReturn(Optional.of(seat));
    when(bookingRepository.saveAndFlush(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

    // act
    Booking result = bookingService.createBooking(new CreateBookingRequest("KL123", "1A"), owner);

    // assert
    assertThat(seat.getStatus()).isEqualTo(SeatStatus.BOOKED);
    assertThat(result.getSeat()).isSameAs(seat);
    assertThat(result.getUser()).isSameAs(owner);
    verify(seatRepository).saveAndFlush(seat);
  }

  @Test
  void createBooking_seatAlreadyBooked_throwsSeatUnavailable() {
    seat.setStatus(SeatStatus.BOOKED);
    when(flightRepository.findByFlightNumber("KL123")).thenReturn(Optional.of(futureFlight));
    when(seatRepository.findByFlightAndSeatNumber(futureFlight, "1A")).thenReturn(Optional.of(seat));

    assertThatThrownBy(() -> bookingService.createBooking(new CreateBookingRequest("KL123", "1A"), owner))
        .isInstanceOf(SeatUnavailableException.class);

    verify(seatRepository, never()).saveAndFlush(any());
    verify(bookingRepository, never()).saveAndFlush(any());
  }

  @Test
  void createBooking_departedFlight_throwsConflict() {
    futureFlight.setDepartureTime(LocalDateTime.now().minusHours(1));
    when(flightRepository.findByFlightNumber("KL123")).thenReturn(Optional.of(futureFlight));
    when(seatRepository.findByFlightAndSeatNumber(futureFlight, "1A")).thenReturn(Optional.of(seat));

    assertThatThrownBy(() -> bookingService.createBooking(new CreateBookingRequest("KL123", "1A"), owner))
        .isInstanceOf(ConflictDataInputException.class);

    verify(bookingRepository, never()).saveAndFlush(any());
  }

  @Test
  void createBooking_unknownFlight_throwsNotFound() {
    when(flightRepository.findByFlightNumber("XX999")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookingService.createBooking(new CreateBookingRequest("XX999", "1A"), owner))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void createBooking_optimisticLockFailure_throwsSeatUnavailable() {
    // Simulates another user booking the same seat a split second earlier
    when(flightRepository.findByFlightNumber("KL123")).thenReturn(Optional.of(futureFlight));
    when(seatRepository.findByFlightAndSeatNumber(futureFlight, "1A")).thenReturn(Optional.of(seat));
    when(seatRepository.saveAndFlush(seat))
        .thenThrow(new ObjectOptimisticLockingFailureException(Seat.class, 1L));

    assertThatThrownBy(() -> bookingService.createBooking(new CreateBookingRequest("KL123", "1A"), owner))
        .isInstanceOf(SeatUnavailableException.class);

    verify(bookingRepository, never()).saveAndFlush(any());
  }

  // ---------- getBooking (ownership) ----------

  @Test
  void getBooking_owner_returnsBooking() {
    Booking booking = booking(owner, seat);
    when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

    assertThat(bookingService.getBooking(10L, owner)).isSameAs(booking);
  }

  @Test
  void getBooking_nonOwnerClient_throwsAccessDenied() {
    when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking(owner, seat)));

    assertThatThrownBy(() -> bookingService.getBooking(10L, stranger))
        .isInstanceOf(AccessDeniedException.class);
  }

  @Test
  void getBooking_adminNotOwner_returnsBooking() {
    Booking booking = booking(owner, seat);
    when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

    assertThat(bookingService.getBooking(10L, admin)).isSameAs(booking);
  }

  @Test
  void getBooking_unknownId_throwsNotFound() {
    when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> bookingService.getBooking(99L, owner))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // ---------- deleteBooking (cancel) ----------

  @Test
  void deleteBooking_owner_releasesSeatAndDeletesBooking() {
    seat.setStatus(SeatStatus.BOOKED);
    Booking booking = booking(owner, seat);
    when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));

    bookingService.deleteBooking(10L, owner);

    assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    verify(bookingRepository).delete(booking);
  }

  @Test
  void deleteBooking_nonOwnerClient_throwsAndKeepsSeatBooked() {
    seat.setStatus(SeatStatus.BOOKED);
    when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking(owner, seat)));

    assertThatThrownBy(() -> bookingService.deleteBooking(10L, stranger))
        .isInstanceOf(AccessDeniedException.class);

    assertThat(seat.getStatus()).isEqualTo(SeatStatus.BOOKED);
    verify(bookingRepository, never()).delete(any());
  }

  // ---------- updateSeatBooking ----------

  @Test
  void updateSeatBooking_newSeatAvailable_swapsSeats() {
    seat.setStatus(SeatStatus.BOOKED);
    Seat newSeat = new Seat("1B", SeatStatus.AVAILABLE, futureFlight);
    Booking booking = booking(owner, seat);

    when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
    when(seatRepository.findByFlightAndSeatNumber(futureFlight, "1B")).thenReturn(Optional.of(newSeat));
    when(bookingRepository.save(booking)).thenReturn(booking);

    Booking result = bookingService.updateSeatBooking(10L, "1B", owner);

    assertThat(result.getSeat()).isSameAs(newSeat);
    assertThat(newSeat.getStatus()).isEqualTo(SeatStatus.BOOKED);
    assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
  }

  @Test
  void updateSeatBooking_newSeatTaken_throwsAndKeepsOldSeat() {
    seat.setStatus(SeatStatus.BOOKED);
    Seat takenSeat = new Seat("1B", SeatStatus.BOOKED, futureFlight);
    Booking booking = booking(owner, seat);

    when(bookingRepository.findById(10L)).thenReturn(Optional.of(booking));
    when(seatRepository.findByFlightAndSeatNumber(futureFlight, "1B")).thenReturn(Optional.of(takenSeat));

    assertThatThrownBy(() -> bookingService.updateSeatBooking(10L, "1B", owner))
        .isInstanceOf(SeatUnavailableException.class);

    assertThat(booking.getSeat()).isSameAs(seat);
    assertThat(seat.getStatus()).isEqualTo(SeatStatus.BOOKED);
    verify(bookingRepository, never()).save(any());
  }

  // ---------- helpers ----------

  private static User user(long id, UserRole role) {
    User u = new User();
    ReflectionTestUtils.setField(u, "id", id);
    u.setRole(role);
    return u;
  }

  private static Booking booking(User user, Seat seat) {
    return new Booking(user, LocalDateTime.now(), seat, BookingStatus.CONFIRMED);
  }
}