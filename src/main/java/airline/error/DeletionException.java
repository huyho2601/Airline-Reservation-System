package airline.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (HttpStatus.CONFLICT)
public class DeletionException extends RuntimeException {
  public DeletionException(String message) {
    super(message);
  }
  
}
