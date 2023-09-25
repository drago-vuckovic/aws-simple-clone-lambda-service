package co.vuckovic.lambdaservice.repository.entity;

import co.vuckovic.lambdaservice.model.enumeration.LambdaLang;
import co.vuckovic.lambdaservice.model.enumeration.TriggerType;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "Lambda")
public class LambdaEntity {

  @Id
  @GeneratedValue
  private Integer id;
  private String name;
  private String srcPath;
  private String destPath;

  @Enumerated(EnumType.ORDINAL)
  private TriggerType triggerType;

  private String lambdaFilePath;
  private Boolean isEnabled;

  @Enumerated(EnumType.ORDINAL)
  private LambdaLang lambdaLang;

  private String createdBy;
  private Timestamp creationTime;
  private String description;
  private Integer tenantId;
  @OneToMany(mappedBy = "lambdaId")
  private List<LogEntity> logEntities;
}
