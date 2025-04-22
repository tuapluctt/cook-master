package vn.hvt.cook_master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hvt.cook_master.entity.StepImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface StepImageRepository extends JpaRepository<StepImage, String> {
    @Query("SELECT MAX(i.displayOrder) FROM StepImage i WHERE i.step.stepId = :stepId")
    Integer findMaxDisplayOrderByStepId(@Param("stepId") Long stepId);

    List<StepImage> findByStep_StepId(Long stepId);

}
