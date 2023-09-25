package co.vuckovic.lambdaservice.model.request;

import java.sql.Timestamp;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionTimeIntervalRequest {

  @NotBlank
  private Timestamp startTime;
  @NotBlank
  private Timestamp endTime;
}
