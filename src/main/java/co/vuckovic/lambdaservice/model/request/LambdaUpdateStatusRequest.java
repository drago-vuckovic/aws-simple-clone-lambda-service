package co.vuckovic.lambdaservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LambdaUpdateStatusRequest {

  private Boolean isEnabled;

}
