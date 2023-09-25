package co.vuckovic.lambdaservice.advice;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    log.error(String.format("Validation exception occurred: %s", e.getMessage()));

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(HttpMessageNotReadableException.class)
  public final ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException e) {
    log.error(String.format("Message is not readable: %s", e.getMessage()));
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }


  @ExceptionHandler(Exception.class)
  public final ResponseEntity<Object> handleException(Exception e) {
    log.error(String.format("An error occurred while performing action: %s", e.getMessage()));
    return new ResponseEntity<>(
        String.format("An error occurred while performing action: %s", e.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
