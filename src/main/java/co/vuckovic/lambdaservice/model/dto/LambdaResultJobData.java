package co.vuckovic.lambdaservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LambdaResultJobData {

  private byte[] fileBytes;
  private String filePath;
  private String ownerEmail;
}
