package co.vuckovic.lambdaservice.repository;

import co.vuckovic.lambdaservice.repository.entity.LambdaEntity;
import co.vuckovic.lambdaservice.model.enumeration.TriggerType;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LambdaEntityRepository extends JpaRepository<LambdaEntity, Integer> {

  List<LambdaEntity> findAllBySrcPathAndTriggerTypeAndIsEnabledTrue(String srcPath, TriggerType triggerType);
  boolean existsBySrcPathAndTriggerTypeAndIsEnabledTrue(String srcPath, TriggerType triggerType);

  List<LambdaEntity> findAllByTenantId(Integer tenantId);
  List<LambdaEntity> findAllBySrcPath(String path);
  List<LambdaEntity> findAllByDestPath(String path);
  List<LambdaEntity> findAllBySrcPathOrDestPath(String srcPath, String destPath);

}
