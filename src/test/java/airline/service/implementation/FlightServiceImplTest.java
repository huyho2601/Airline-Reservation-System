package airline.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import airline.dto.CreateFlightRequest;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.enums.SeatStatus;
import airline.error.DeletionException;
import airline.error.DuplicateResourceException;
import airline.error.ResourceNotFoundException;
import airline.repository.FlightRepository;
import airline.repository.SeatRepository;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

  @Mock FlightRepository flightRepository;
  @Mock SeatRepository seatRepository;

  @InjectMocks FlightServiceImpl flightService;

  @Captor ArgumentCaptor<List<Seat>> seatsCaptor;

  private Flight flight;

  @BeforeEach
  void setUp() {
    flight = new Flight();
    flight.setFlightNumber("KL123");
    flight.setDepartureTime(LocalDateTime.now().plusDays(1));
  }

  // ---------- createFlight ----------

  @Test
  void createFlight_duplicateFlightNumber_throwsDuplicate() {
    CreateFlightRequest request = mock(CreateFlightRequest.class);
    when(request.getFlightNumber()).thenReturn("KL123");
    when(flightRepository.existsByFlightNumber("KL123")).thenReturn(true);

    assertThatThrownBy(() -> flightService.createFlight(request))
        .isInstanceOf(DuplicateResourceException.class);

    verify(flightRepository, never()).save(any());
    verify(seatRepository, never()).saveAll(any());
  }

  @Test
  void createFlight_valid_savesFlightAndGeneratesSeats() {
    // 8 seats -> row 1 gets A-F, row 2 gets A-B
    CreateFlightRequest request = mock(CreateFlightRequest.class);
    when(request.getFlightNumber()).thenReturn("KL123");
    when(request.getTotalSeats()).thenReturn(8);
    when(flightRepository.existsByFlightNumber("KL123")).thenReturn(false);
    when(flightRepository.save(any(Flight.class))).thenAnswer(inv -> inv.getArgument(0));

    Flight saved = flightService.createFlight(request);

    verify(seatRepository).saveAll(seatsCaptor.capture());
    List<Seat> seats = seatsCaptor.getValue();

    assertThat(saved.getFlightNumber()).isEqualTo("KL123");
    assertThat(seats).extracting(Seat::getSeatNumber)
        .containsExactly("1A", "1B", "1C", "1D", "1E", "1F", "2A", "2B");
    assertThat(seats).allSatisfy(s -> {
      assertThat(s.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
      assertThat(s.getFlight()).isSameAs(saved);
    });
  }

  // ---------- getFlightByFlightNumber ----------

  @Test
  void getFlightByFlightNumber_unknown_throwsNotFound() {
    when(flightRepository.findByFlightNumber("XX999")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> flightService.getFlightByFlightNumber("XX999"))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  // ---------- deleteFlight ----------

  @Test
  void deleteFlight_withBookedSeats_throwsDeletion() {
    when(flightRepository.findByFlightNumber("KL123")).thenReturn(Optional.of(flight));
    when(seatRepository.existsByFlightAndStatusNot(flight, SeatStatus.AVAILABLE)).thenReturn(true);

    assertThatThrownBy(() -> flightService.deleteFlight("KL123"))
        .isInstanceOf(DeletionException.class);

    verify(seatRepository, never()).deleteByFlight(any());
    verify(flightRepository, never()).delete(any());
  }

  @Test
  void deleteFlight_noBookedSeats_deletesSeatsThenFlight() {
    when(flightRepository.findByFlightNumber("KL123")).thenReturn(Optional.of(flight));
    when(seatRepository.existsByFlightAndStatusNot(flight, SeatStatus.AVAILABLE)).thenReturn(false);

    flightService.deleteFlight("KL123");

    // Seats must go first, or the foreign key from seat -> flight blocks the delete
    InOrder order = inOrder(seatRepository, flightRepository);
    order.verify(seatRepository).deleteByFlight(flight);
    order.verify(flightRepository).delete(flight);
  }
}