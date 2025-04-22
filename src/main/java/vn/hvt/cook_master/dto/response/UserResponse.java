package vn.hvt.cook_master.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.hvt.cook_master.entity.Role;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String fullName;
    String email;
    String nickName;
    Set<Role> roles;

}

