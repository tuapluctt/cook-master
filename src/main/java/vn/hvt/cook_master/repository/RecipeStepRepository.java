package vn.hvt.cook_master.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hvt.cook_master.entity.RecipeStep;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, String> {
    @Query("SELECT MAX(s.stepNumber) FROM RecipeStep s WHERE s.recipe.recipeId = :recipeId")
    Integer findMaxStepNumberByRecipeId(@Param("recipeId") Long recipeId);
}
