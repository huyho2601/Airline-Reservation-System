package airline;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import airline.dto.CreateBookingRequest;
import airline.entity.Flight;
import airline.entity.Seat;
import airline.entity.User;
import airline.entity.enums.SeatStatus;
import airline.entity.enums.UserRole;
import airline.error.SeatUnavailableException;
import airline.repository.BookingRepository;
import airline.repository.FlightRepository;
import airline.repository.SeatRepository;
import airline.repository.UserRepository;
import airline.service.BookingService;

/**
 * Full-app test against a real MySQL test database (see application-test.properties).
 *
 * NOT @Transactional on purpose: every thread must run in its own real
 */
@SpringBootTest
@ActiveProfiles("test")
class BookingConcurrencyTest {

  private static final int THREADS = 10;

  @Autowired BookingService bookingService;
  @Autowired BookingRepository bookingRepository;
  @Autowired SeatRepository seatRepository;
  @Autowired FlightRepository flightRepository;
  @Autowired UserRepository userRepository;

  private final List<User> users = new ArrayList<>();

  @BeforeEach
  void setUp() {
    cleanDatabase();

    Flight flight = new Flight();
    flight.setFlightNumber("KL123");
    flight.setOrigin("Amsterdam");
    flight.setDestination("London");
    flight.setDepartureTime(LocalDateTime.now().plusDays(7));
    flight.setArrivalTime(LocalDateTime.now().plusDays(7).plusHours(1));
    flight.setPrice(new BigDecimal("150.0"));
    flight.setTotalSeats(1);
    flight = flightRepository.save(flight);

    seatRepository.save(new Seat("1A", SeatStatus.AVAILABLE, flight));

    // One different user per thread, so ownership never matters
    for (int i = 0; i < THREADS; i++) {
      User u = new User();
      u.setName("User " + i);
      u.setUsername("racer" + i);
      u.setUserEmail("racer" + i + "@example.com");
      u.setPassword("irrelevant");
      u.setRole(UserRole.CLIENT);
      users.add(userRepository.save(u));
    }
  }

  @AfterEach
  void tearDown() {
    cleanDatabase();
  }

  @Test
  void createBooking_manyUsersSameSeatAtOnce_exactlyOneSucceeds() throws Exception {
    ExecutorService pool = Executors.newFixedThreadPool(THREADS);
    CountDownLatch ready = new CountDownLatch(THREADS); // every thread is waiting at the gate
    CountDownLatch start = new CountDownLatch(1);       // the starting pistol

    AtomicInteger successes = new AtomicInteger();
    AtomicInteger seatUnavailable = new AtomicInteger();
    Queue<Throwable> unexpected = new ConcurrentLinkedQueue<>();

    List<Future<?>> futures = new ArrayList<>();
    for (User user : users) {
      futures.add(pool.submit(() -> {
        ready.countDown();
        try {
          start.await(); // all threads block here until the pistol fires
          bookingService.createBooking(new CreateBookingRequest("KL123", "1A"), user);
          successes.incrementAndGet();
        } catch (SeatUnavailableException e) {
          seatUnavailable.incrementAndGet();
        } catch (Throwable t) {
          unexpected.add(t);
        }
        return null;
      }));
    }

    ready.await(10, TimeUnit.SECONDS); // wait until every thread is at the gate
    start.countDown();                 // fire: all threads book at the same moment

    for (Future<?> f : futures) {
      f.get(30, TimeUnit.SECONDS);
    }
    pool.shutdown();

    // Any exception other than SeatUnavailableException means the race
    // was not handled cleanly (e.g. a raw lock or deadlock error leaked out)
    assertThat(unexpected).as("unexpected exceptions").isEmpty();

    assertThat(successes.get()).as("successful bookings").isEqualTo(1);
    assertThat(seatUnavailable.get()).as("rejected bookings").isEqualTo(THREADS - 1);

    // The database is the final judge: exactly one booking row, seat is BOOKED
    assertThat(bookingRepository.count()).isEqualTo(1);
    Seat seat = seatRepository.findAll().get(0);
    assertThat(seat.getStatus()).isEqualTo(SeatStatus.BOOKED);
  }

  // Order matters: children before parents because of foreign keys
  private void cleanDatabase() {
    bookingRepository.deleteAll();
    seatRepository.deleteAll();
    flightRepository.deleteAll();
    userRepository.deleteAll();
    users.clear();
  }
}