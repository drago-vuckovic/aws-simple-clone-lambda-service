package co.vuckovic.lambdaservice.service;

import co.vuckovic.lambdaservice.repository.LambdaEntityRepository;
import co.vuckovic.lambdaservice.repository.entity.LambdaEntity;
import co.vuckovic.lambdaservice.config.LambdaProperties;
import co.vuckovic.lambdaservice.model.dto.LambdaJobData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Service;

@EnableKafka
@Slf4j
@Configuration
@Service
@RequiredArgsConstructor
public class LambdaTopicConsumer {

  private final LambdaEntityRepository lambdaEntityRepository;

  private final LambdaService lambdaService;

  private final LambdaProperties lambdaProperties;

  @Bean
  public NewTopic lambdaTopic() {
    NewTopic newTopic = TopicBuilder.name("lambda-topic").build();
    Map<String, String> configs = new HashMap<>();
    newTopic.configs(configs);
    return newTopic;
  }

  @KafkaListener(topics = "lambda-topic", groupId = "lambda")
  public void getMessages(LambdaJobData lambdaJobData) {
    List<LambdaEntity> lambdaEntities = lambdaEntityRepository.findAllBySrcPathAndTriggerTypeAndIsEnabledTrue(
        lambdaJobData.getSrcPath(), lambdaJobData.getTriggerType());

    if (lambdaEntities != null) {
      lambdaEntities.forEach(lambdaEntity -> {
        String destDirPath =
            lambdaProperties.getFakeDestDir() + File.separator + lambdaEntity.getId();
        lambdaService.executeLambda(lambdaEntity.getLambdaLang(),
            lambdaEntity.getLambdaFilePath(), destDirPath, lambdaJobData.getFileBytes(),
            lambdaJobData.getFileName(), lambdaEntity.getId(), lambdaEntity.getTenantId());
      });
    }
  }

}
