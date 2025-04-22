package vn.hvt.cook_master.mapper;

import org.mapstruct.Mapper;
import vn.hvt.cook_master.dto.response.StepResponse;
import vn.hvt.cook_master.entity.RecipeStep;
import vn.hvt.cook_master.entity.StepImage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecipeStepMapper {
    StepResponse toStepResponse(RecipeStep step);
    List<StepResponse> toStepResponse(List<RecipeStep> steps);
}
