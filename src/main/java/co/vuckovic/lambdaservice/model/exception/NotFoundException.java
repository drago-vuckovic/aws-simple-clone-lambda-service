package co.vuckovic.lambdaservice.model.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
public class NotFoundException extends HttpException {

  public NotFoundException(Object data) {
    super(HttpStatus.NOT_FOUND, data);
  }
}
