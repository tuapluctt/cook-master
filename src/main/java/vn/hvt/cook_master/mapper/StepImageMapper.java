package vn.hvt.cook_master.mapper;

import org.mapstruct.Mapper;
import vn.hvt.cook_master.dto.response.StepImageResponse;
import vn.hvt.cook_master.dto.response.StepResponse;
import vn.hvt.cook_master.entity.RecipeStep;
import vn.hvt.cook_master.entity.StepImage;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StepImageMapper {
    StepImageResponse toStepResponse(StepImage stepImage);
    List<StepImageResponse> toStepResponse(List<StepImage> stepImages);
}
