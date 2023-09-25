package co.vuckovic.lambdaservice.watcher;

import co.vuckovic.lambdaservice.repository.LambdaEntityRepository;
import co.vuckovic.lambdaservice.repository.entity.LambdaEntity;
import co.vuckovic.lambdaservice.config.LambdaProperties;
import co.vuckovic.lambdaservice.model.dto.LambdaResultJobData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFile.Type;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyFileChangeListener implements FileChangeListener {

  private final LambdaProperties lambdaProperties;

  private final LambdaEntityRepository lambdaEntityRepository;

  private final KafkaTemplate<String, LambdaResultJobData> kafkaTemplate;

  @Override
  public void onChange(Set<ChangedFiles> changeSet) {
    for (ChangedFiles cfiles : changeSet) {
      for (ChangedFile cfile : cfiles.getFiles()) {
        if (cfile.getType().equals(Type.ADD) || cfile.getType().equals(Type.MODIFY)) {
          File file = cfile.getFile();
          log.info("Operation: " + cfile.getType() + " On file: " + file.getName() + " is done");
          LambdaEntity lambdaEntity = lambdaEntityRepository.findById(
                  Integer.parseInt(file.getParentFile().getName()))
              .orElseThrow(() -> new NotFoundException("Lambda not found!"));
          try {
            LambdaResultJobData lambdaResultJobData = LambdaResultJobData.builder().fileBytes(
                    Files.readAllBytes(file.toPath()))
                .filePath(lambdaEntity.getDestPath() + File.separator + file.getName())
                .ownerEmail(lambdaEntity.getCreatedBy()).build();
            sendMessageToTopic(lambdaResultJobData);
            file.delete();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
  }

  private void sendMessageToTopic(LambdaResultJobData lambdaResultJobData) {
    kafkaTemplate.send("lambda-result-topic", lambdaResultJobData);
  }
}