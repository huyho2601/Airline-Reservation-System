package airline.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

public class CreateFlightRequest {
  private String flightNumber;
  private String origin;
  private String destination;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime departureTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime arrivalTime;
  
  private BigDecimal price;
  private int totalSeats;

  // Constructor
  public CreateFlightRequest(String origin, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime,
      BigDecimal price, String flightNumber, int totalSeats) {
    this.flightNumber = flightNumber;
    this.origin = origin;
    this.destination = destination;
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
    this.price = price;
    this.totalSeats = totalSeats;
  }

  public CreateFlightRequest(){}

  // Getters and Setters
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

  public String getFlightNumber() {
    return flightNumber;
  }

  public void setFlightNumber(String flightNumber) {
    this.flightNumber = flightNumber;
  }

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeat) {
		this.totalSeats = totalSeat;
	}

  
  
}
