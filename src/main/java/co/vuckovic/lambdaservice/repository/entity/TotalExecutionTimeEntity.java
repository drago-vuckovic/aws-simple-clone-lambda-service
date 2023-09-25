package co.vuckovic.lambdaservice.repository.entity;

import java.sql.Date;
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
@Table(name = "TotalExecutionTime")
public class TotalExecutionTimeEntity {

  @Id
  @GeneratedValue
  private Integer id;
  private Integer duration;
  private Integer tenantId;
  private Integer maxDuration;
  private Date periodStart;
}
