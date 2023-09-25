package co.vuckovic.service;

import co.vuckovic.repository.TotalExecutionTimeEntityRepository;

import java.sql.Date;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyService {

  private final TotalExecutionTimeEntityRepository totalExecutionTimeEntityRepository;

  @Scheduled(cron = "0 0 * * * *")
  private void verificationTask() {
    LocalDate localDate = LocalDate.now();
    LocalDate start = localDate.minusMonths(1);
    Date date = Date.valueOf(start);
    totalExecutionTimeEntityRepository.findAllByPeriodStart(date)
        .forEach(totalExecutionTimeEntity -> {
          totalExecutionTimeEntity.setDuration(0);
          totalExecutionTimeEntity.setPeriodStart(Date.valueOf(localDate));
          totalExecutionTimeEntityRepository.save(totalExecutionTimeEntity);
        });
  }

}
