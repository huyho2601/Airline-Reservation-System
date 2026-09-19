package airline.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import airline.entity.enums.BookingStatus;

@Entity
@Table(name = "bookings")
public class Booking {
  
  @Id
  // @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;

  @ManyToOne
  @JoinColumn (name = "user_id", nullable = false)
  private User user;

  @Column(name = "booking_time", nullable = false)
  private LocalDateTime bookingTime;

  @ManyToOne 
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  @Enumerated (EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  // Constructors
  public Booking() {
  }

  public Booking(User user, LocalDateTime bookingTime, Seat seat, BookingStatus status) {
    this.user = user;
    this.bookingTime = bookingTime;
    this.seat = seat;
    this.status = status;
  }

  // Getters and Setters
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getBookingTime() {
    return bookingTime;
  }
  public void setBookingTime(LocalDateTime bookingTime) {
    this.bookingTime = bookingTime;
  }

  public Seat getSeat() {
    return seat;
  }
  public void setSeat(Seat seat) {
    this.seat = seat;
  }

  public BookingStatus getStatus() {
    return status;
  }
  public void setStatus(BookingStatus status) {
    this.status = status;
  }

}
