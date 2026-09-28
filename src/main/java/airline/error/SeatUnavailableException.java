package airline.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SeatUnavailableException extends RuntimeException {
  public SeatUnavailableException(String msg) {
    super(msg);
  }
}
