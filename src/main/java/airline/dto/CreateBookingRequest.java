package airline.dto;

public class CreateBookingRequest {

  private Long seatId;

  public CreateBookingRequest() {
  }

  public CreateBookingRequest(Long seatId) {
    this.seatId = seatId;
  }

  public Long getSeatId() {
    return seatId;
  }

  public void setSeatId(Long seatId) {
    this.seatId = seatId;
  }
}
