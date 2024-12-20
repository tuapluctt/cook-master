package vn.hvt.cook_master.service;

import vn.hvt.cook_master.dto.request.RoleRequest;
import vn.hvt.cook_master.dto.response.RoleResponse;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleResponse> getAllRoles();
    RoleResponse createRole(RoleRequest roleRequest);
    void addPermissionToRole(String roleName, String permissionName);
    void removePermissionFromRole(String roleName, String permissionName);
    void deleteRole(String roleName);
}
