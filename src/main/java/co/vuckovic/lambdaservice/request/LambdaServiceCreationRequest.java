package co.vuckovic.lambdaservice.request;

import co.vuckovic.lambdaservice.model.enumeration.LambdaLang;
import co.vuckovic.lambdaservice.model.enumeration.TriggerType;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class LambdaServiceCreationRequest {

  private String name;
  private String srcPath;
  private String destPath;
  private TriggerType triggerType;
  private LambdaLang lambdaLang;
  private String createdBy;
  private Timestamp creationTime;
  private String description;
  private Integer tenantId;
}
