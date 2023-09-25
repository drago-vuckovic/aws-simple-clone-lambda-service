package co.vuckovic.lambdaservice.repository;

import co.vuckovic.lambdaservice.repository.entity.LogEntity;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LogEntityRepository extends JpaRepository<LogEntity, Integer> {

  @Query(value = "SELECT name, ROUND(SUM(EXTRACT("
      + " EPOCH FROM ("
      + " CASE"
      + " WHEN end_time IS NULL THEN :endTime"
      + " WHEN end_time<:endTime THEN end_time"
      + " ELSE :endTime"
      + " END"
      + " - "
      + " CASE"
      + " WHEN start_time < :startTime THEN :startTime"
      + " ELSE start_time"
      + " END"
      + " )"
      + ")"
      + ")) as duration"
      + " FROM log"
      + " INNER JOIN lambda on lambda_id=lambda.id"
      + " WHERE ((start_time>:startTime AND start_time<:endTime) OR"
      + " (end_time>:startTime AND end_time<:endTime) OR"
      + " (start_time<:startTime AND end_time IS NULL))"
      + " AND tenant_id=:tenantId"
      + " GROUP BY name;", nativeQuery = true)
  List<LambdaExecutionTime> findAllLambdaExecutionDurations(Timestamp startTime, Timestamp endTime, Integer tenantId);

  interface LambdaExecutionTime {

    String getName();

    Double getDuration();
  }
}
