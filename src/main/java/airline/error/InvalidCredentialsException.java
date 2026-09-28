package airline.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_MODIFIED)
public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String msg) {
    super(msg);
  }
}
