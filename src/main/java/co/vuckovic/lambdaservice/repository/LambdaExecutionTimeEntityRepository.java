package co.vuckovic.lambdaservice.repository;

import co.vuckovic.lambdaservice.repository.entity.LambdaExecutionTimeEntity;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LambdaExecutionTimeEntityRepository extends
    JpaRepository<LambdaExecutionTimeEntity, Integer> {


  @Query(value = "SELECT name ,SUM(duration) AS duration"
      + " FROM lambda_execution_time "
      + " INNER JOIN lambda"
      + " ON lambda_id=lambda.id"
      + " WHERE execution_date>=:startDate AND execution_date <= :endDate AND tenant_id = :tenantId"
      + " GROUP BY name;", nativeQuery = true)
  List<LambdaExecutionTime> findAllBetweenDates(Timestamp startDate,
      Timestamp endDate, Integer tenantId);

  interface LambdaExecutionTime {

    String getName();

    BigInteger getDuration();
  }

}
