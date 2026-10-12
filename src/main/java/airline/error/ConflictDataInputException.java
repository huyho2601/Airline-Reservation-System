package airline.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictDataInputException extends RuntimeException {
  public ConflictDataInputException(String message) {
    super(message);
  }

}
