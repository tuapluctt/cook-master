package vn.hvt.cook_master.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientRequest {
    @NotBlank(message = "NOT_BLANK")
    String name;
    @NotBlank(message = "NOT_BLANK")
    @Positive(message = "POSITIVE") // so dương
    Double quantity;
    @NotBlank(message = "NOT_BLANK")
    String unit;
}
