package co.vuckovic.lambdaservice.model.exception;

import org.springframework.http.HttpStatus;

public class ProcessExecutionException extends HttpException {

  public ProcessExecutionException(Object data) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, data);
  }
}
