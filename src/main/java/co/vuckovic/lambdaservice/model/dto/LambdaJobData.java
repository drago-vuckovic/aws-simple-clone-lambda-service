package co.vuckovic.lambdaservice.model.dto;

import co.vuckovic.lambdaservice.model.enumeration.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LambdaJobData {

  private String srcPath;
  private TriggerType triggerType;
  private String fileName;
  private byte[] fileBytes;
  private Integer tenantId;
}
