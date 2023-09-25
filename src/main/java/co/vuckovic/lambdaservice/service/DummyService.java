package co.vuckovic.lambdaservice.service;

import co.vuckovic.lambdaservice.repository.TotalExecutionTimeEntityRepository;
import co.vuckovic.lambdaservice.repository.entity.TotalExecutionTimeEntity;
import co.vuckovic.lambdaservice.model.dto.DummyPair;

import java.sql.Date;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DummyService {

  private final TotalExecutionTimeEntityRepository totalExecutionTimeEntityRepository;

  public void populateDB(List<DummyPair> dummyPairs) {
    dummyPairs.forEach(pair -> {
      if (!totalExecutionTimeEntityRepository.existsByTenantId(pair.getTenantId())) {
        TotalExecutionTimeEntity totalExecutionTimeEntity = TotalExecutionTimeEntity.builder().id(0)
            .duration(0).periodStart(new Date(System.currentTimeMillis())).tenantId(
                pair.getTenantId()).build();
        if (pair.getSubscriptionType().equals("DEFAULT")) {
          totalExecutionTimeEntity.setMaxDuration(3600000);
        }
        totalExecutionTimeEntityRepository.save(totalExecutionTimeEntity);
      }
    });
  }
}
