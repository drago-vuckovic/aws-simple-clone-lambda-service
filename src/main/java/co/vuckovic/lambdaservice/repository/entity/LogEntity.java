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
@Table(name = "Log")
public class LogEntity {

  @Id
  @GeneratedValue
  private Integer id;
  private Timestamp startTime;
  private Timestamp endTime;
  private Integer lambdaId;
}
