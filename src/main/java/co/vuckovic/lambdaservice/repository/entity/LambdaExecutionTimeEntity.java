package co.vuckovic.lambdaservice.repository.entity;

import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LambdaExecutionTime")
public class LambdaExecutionTimeEntity {

  @Id
  @GeneratedValue
  private Integer id;
  private Timestamp executionDate;
  private Integer duration;
  private Integer lambdaId;
}
