package vn.hvt.cook_master.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.entity.RecipeStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeResponse {
     String title;
     String description;
     String cookTime;
     String status;
     List<IngredientResponse> ingredients;
     List<StepResponse> steps;
}

