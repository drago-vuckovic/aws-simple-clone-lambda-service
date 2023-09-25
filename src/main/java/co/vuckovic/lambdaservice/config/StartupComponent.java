package co.vuckovic.lambdaservice.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupComponent implements CommandLineRunner {

  private final LambdaProperties lambdaProperties;

  private final DockerProperties dockerProperties;

  @Override
  public void run(String... args) throws Exception {
    Files.createDirectories(Paths.get(lambdaProperties.getDirPath()));
    Files.createDirectories(Paths.get(lambdaProperties.getFakeDestDir()));
    Files.createDirectories(Paths.get(dockerProperties.getDirPathWin()));
  }
}