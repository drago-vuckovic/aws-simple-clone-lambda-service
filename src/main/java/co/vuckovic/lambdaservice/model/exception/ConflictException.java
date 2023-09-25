package co.vuckovic.lambdaservice.model.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends HttpException {

  public ConflictException(Object data) {
    super(HttpStatus.CONFLICT, data);
  }
}
