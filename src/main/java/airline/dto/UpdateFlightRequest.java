package airline.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateFlightRequest {
  private String origin;
  private String destination;
  private LocalDateTime departureTime;
  private LocalDateTime arrivalTime;
  private BigDecimal price;

  // Constructure
  public UpdateFlightRequest(String origin, String destination, LocalDateTime departureTime,
      LocalDateTime arrivalTime, BigDecimal price) {
    this.origin = origin;
    this.destination = destination;
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
    this.price = price;
  }

  // Setters and Getters
  public String getOrigin() {
    return origin;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public LocalDateTime getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(LocalDateTime departureTime) {
    this.departureTime = departureTime;
  }

  public LocalDateTime getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(LocalDateTime arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

}
