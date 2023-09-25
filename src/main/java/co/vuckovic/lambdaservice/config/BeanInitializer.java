package co.vuckovic.lambdaservice.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanInitializer {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setAmbiguityIgnored(true);
    return mapper;
  }

  @Bean
  public NewTopic lambdaResultTopic() {
    NewTopic newTopic = new NewTopic("lambda-result-topic", 1, (short) 1);
    Map<String, String> configs = new HashMap<>();
    newTopic.configs(configs);
    return newTopic;
  }
}
