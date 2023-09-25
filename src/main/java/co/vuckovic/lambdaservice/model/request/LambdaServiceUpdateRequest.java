package co.vuckovic.lambdaservice.model.request;

import co.vuckovic.lambdaservice.model.enumeration.LambdaLang;
import co.vuckovic.lambdaservice.model.enumeration.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LambdaServiceUpdateRequest {

  private String name;
  private String srcPath;
  private String destPath;
  private TriggerType triggerType;
  private LambdaLang lambdaLang;
  private String currentUserEmail;
  private String description;

}
