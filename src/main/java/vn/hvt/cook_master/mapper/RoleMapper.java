package vn.hvt.cook_master.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.hvt.cook_master.dto.request.RoleRequest;
import vn.hvt.cook_master.dto.response.RoleResponse;
import vn.hvt.cook_master.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
