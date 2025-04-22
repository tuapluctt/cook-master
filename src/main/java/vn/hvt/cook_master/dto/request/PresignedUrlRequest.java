package vn.hvt.cook_master.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.hvt.cook_master.entity.RecipeStatus;
import vn.hvt.cook_master.enums.AllowedFileType;
import vn.hvt.cook_master.validator.EnumSubset;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PresignedUrlRequest {
    @NotBlank(message = "NOT_BLANK")
    String fileName;

    @EnumSubset(name = "fileType", enumClass = AllowedFileType.class, message ="ENUM_INVALID")
    String fileType;
}
