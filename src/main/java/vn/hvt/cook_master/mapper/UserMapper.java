package vn.hvt.cook_master.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.hvt.cook_master.dto.request.UserCreateRequest;
import vn.hvt.cook_master.dto.request.UserUpdateRequest;
import vn.hvt.cook_master.dto.response.UserEditResponse;
import vn.hvt.cook_master.dto.response.UserResponse;
import vn.hvt.cook_master.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest userRequest);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user , UserUpdateRequest userUpdateRequest);

    UserEditResponse toUserEditResponse(User user);
}
