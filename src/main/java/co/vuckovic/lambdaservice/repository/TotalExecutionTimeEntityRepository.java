package co.vuckovic.lambdaservice.repository;

import co.vuckovic.lambdaservice.repository.entity.TotalExecutionTimeEntity;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TotalExecutionTimeEntityRepository extends JpaRepository<TotalExecutionTimeEntity, Integer> {


  Optional<TotalExecutionTimeEntity> findByTenantId(Integer integer);

  boolean existsByTenantId(Integer tenantId);

  List<TotalExecutionTimeEntity> findAllByPeriodStart(Date date);

}
