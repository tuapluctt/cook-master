package vn.hvt.cook_master.dto.request;

import jakarta.validation.constraints.Email;
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
public class UserCreateRequest {

    @NotBlank(message = "NOT_BLANK")
    private String username;

    @NotBlank(message = "NOT_BLANK")
    @Email(message = "INVALID_EMAIL")
    private String email;

    @NotBlank(message = "NOT_BLANK")
    private String fullName;

    @NotBlank(message = "NOT_BLANK")
    @Size(min = 8, max = 50, message = "INVALID_PASSWORD")
    private String passwordHash;

}