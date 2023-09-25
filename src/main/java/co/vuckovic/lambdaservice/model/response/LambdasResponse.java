package co.vuckovic.lambdaservice.model.response;

import co.vuckovic.lambdaservice.model.dto.Lambda;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class LambdasResponse {
  private List<Lambda> lambdas;
}
