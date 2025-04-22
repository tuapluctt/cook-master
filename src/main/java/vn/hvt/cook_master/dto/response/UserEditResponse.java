package vn.hvt.cook_master.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEditResponse {
    Long userId;
    String nickName;
    String fullName;
    String email;
    String address;
    String description;
    String avatarUrl;
    long followerCount;
    long followingCount;
}

