package co.vuckovic.lambdaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lambda")
public class LambdaProperties {

  private String dirPath;
  private String pythonPath;
  private String nodejsPath;
  private String fakeSrcDir;
  private String fakeDestDir;
}
