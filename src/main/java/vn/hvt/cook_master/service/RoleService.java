package vn.hvt.cook_master.service;

import vn.hvt.cook_master.dto.request.RoleRequest;
import vn.hvt.cook_master.dto.response.RoleResponse;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleResponse> getAllRoles();
    Optional<RoleResponse> getRoleByName(String roleName);
    RoleResponse createRole(RoleRequest roleRequest);
    RoleResponse updateRole(String roleName, RoleRequest roleRequest);
    void deleteRole(String roleName);
}
