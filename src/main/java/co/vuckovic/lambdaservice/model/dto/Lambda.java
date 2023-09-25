package co.vuckovic.lambdaservice.model.dto;

import co.vuckovic.model.enumeration.LambdaLang;
import co.vuckovic.model.enumeration.TriggerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lambda {

  private Integer id;
  private String name;
  private String createdBy;
  private String srcPath;
  private String destPath;
  private Boolean isEnabled;
  private LambdaLang lambdaLang;
  private String description;
  private TriggerType triggerType;
}
