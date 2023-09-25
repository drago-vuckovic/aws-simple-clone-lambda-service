package co.vuckovic.lambdaservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "docker")
public class DockerProperties {

  private String dirPathWsl;

  private String dirPathWin;

  private String dockerfile;

  private String outputFolder;

  private String workdir;
}
