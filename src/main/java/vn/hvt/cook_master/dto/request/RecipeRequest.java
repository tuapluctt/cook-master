package vn.hvt.cook_master.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.hvt.cook_master.entity.RecipeStatus;
import vn.hvt.cook_master.validator.EnumSubset;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeRequest {
    String title;
    String description;
    String cookTime;

    @EnumSubset(name = "status", enumClass = RecipeStatus.class, message ="ENUM_INVALID")
    String status;
}
