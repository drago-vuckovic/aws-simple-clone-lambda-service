package co.vuckovic.lambdaservice.model.response;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveLambdaExecutionTimeResponse {

  private String name;
  private BigInteger duration;
}
