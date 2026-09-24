package airline.error;

public class SeatUnavailableException extends RuntimeException {

  public SeatUnavailableException(String msg) {
    super(msg);
  }
  
}
