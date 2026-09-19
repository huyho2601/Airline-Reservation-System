package airline.entity;

import airline.entity.enums.SeatStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "seat_number")
  private String seatNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SeatStatus status;

  @ManyToOne
  @JoinColumn (name = "flight_id", nullable = false)
  private Flight flight;

  // Constructors
  public Seat() {
  }

  public Seat(String seatNumber, SeatStatus status, Flight flight) {
    this.seatNumber = seatNumber;
    this.status = status;
    this.flight = flight;
  }

  // Getters and Setters
  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }

  public String getSeatNumber() {
    return seatNumber;
  }
  public void setSeatNumber(String seatNumber) {
    this.seatNumber = seatNumber;
  }

  public SeatStatus getStatus() {
    return status;
  }
  public void setStatus(SeatStatus status) {
    this.status = status;
  }

  public Flight getFlight() {
    return flight;
  }
  public void setFlight(Flight flight) {
    this.flight = flight;
  }

  

}
