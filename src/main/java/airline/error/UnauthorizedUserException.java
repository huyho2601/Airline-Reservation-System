package airline.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (HttpStatus.UNAUTHORIZED)
public class UnauthorizedUserException extends RuntimeException {
  public UnauthorizedUserException (String msg){
    super(msg);
  }
}
