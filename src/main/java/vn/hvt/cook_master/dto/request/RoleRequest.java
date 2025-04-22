package vn.hvt.cook_master.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 3, max = 50, message = "FIELD_INVALID")
    String roleName;
    String description;
    Set<String> permissions;
}