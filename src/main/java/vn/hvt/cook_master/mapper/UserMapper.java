package vn.hvt.cook_master.mapper;

import org.mapstruct.Mapper;
import vn.hvt.cook_master.dto.request.UserCreateRequest;
import vn.hvt.cook_master.dto.response.UserResponse;
import vn.hvt.cook_master.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest userRequest);

    UserResponse toUserResponse(User user);
}
