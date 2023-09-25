package co.vuckovic.lambdaservice.model.request;

import co.vuckovic.lambdaservice.model.enumeration.TriggerType;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LambdaCheckRequest {
  @NotBlank
  private String srcDirPath;
  @NotBlank
  private TriggerType triggerType;
}
