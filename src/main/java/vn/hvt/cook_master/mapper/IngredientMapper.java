package vn.hvt.cook_master.mapper;

import org.mapstruct.Mapper;
import vn.hvt.cook_master.dto.response.IngredientResponse;
import vn.hvt.cook_master.entity.Ingredient;

@Mapper(componentModel = "spring")
public interface IngredientMapper {
    IngredientResponse toIngredientResponse(Ingredient ingredient);
}
