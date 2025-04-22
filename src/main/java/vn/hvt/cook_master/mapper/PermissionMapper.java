package vn.hvt.cook_master.mapper;

import org.mapstruct.Mapper;
import vn.hvt.cook_master.dto.request.PermissionRequest;
import vn.hvt.cook_master.dto.response.PermissionResponse;
import vn.hvt.cook_master.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
}
