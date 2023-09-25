package co.vuckovic.lambdaservice.config;

import co.vuckovic.watcher.MyFileChangeListener;

import java.io.File;
import java.time.Duration;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FileWatcherConfig {

  private final LambdaProperties lambdaProperties;
  private final MyFileChangeListener myFileChangeListener;

  @Bean
  public FileSystemWatcher fileSystemWatcher() {
    FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(true, Duration.ofMillis(1000L),
        Duration.ofMillis(500L));
    fileSystemWatcher.addSourceDirectory(new File(lambdaProperties.getFakeDestDir()));
    fileSystemWatcher.addListener(myFileChangeListener);
    fileSystemWatcher.start();
    log.info("started fileSystemWatcher");
    return fileSystemWatcher;
  }

  @PreDestroy
  public void onDestroy() {
    fileSystemWatcher().stop();
  }
}

