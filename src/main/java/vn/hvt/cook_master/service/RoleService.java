package vn.hvt.cook_master.service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hvt.cook_master.dto.request.RoleRequest;
import vn.hvt.cook_master.dto.response.RoleResponse;
import vn.hvt.cook_master.entity.Permission;
import vn.hvt.cook_master.entity.Role;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.mapper.RoleMapper;
import vn.hvt.cook_master.repository.PermissionRepository;
import vn.hvt.cook_master.repository.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    RoleMapper roleMapper;


    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public RoleResponse createRole(RoleRequest roleRequest) {
        if (roleRepository.existsById(roleRequest.getRoleName())){
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        var role = roleMapper.toRole(roleRequest);
        List<Permission> permissions = permissionRepository.findAllById(roleRequest.getPermissions());

        if (permissions.size() != roleRequest.getPermissions().size()) {
            // lấy ds những permission không tồn tại
            List<String> notFoundPermissions = roleRequest.getPermissions().stream()
                    .filter(permissionName -> permissions.stream().noneMatch(p -> p.getPermissionName().equals(permissionName)))
                    .collect(Collectors.toList());
            throw new AppException(ErrorCode.PERMISSION_NOT_EXISTED, "Permissions not found: " + notFoundPermissions);
        }

        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Transactional
    public void addPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Permission permission = permissionRepository.findById(permissionName)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        // Kiểm tra xem role đã có permission này chưa
        if (role.getPermissions().contains(permission)) {
            throw new AppException(ErrorCode.INVALID_DATA, "Role already has permission: " + permissionName);
        }

        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    @Transactional
    public void removePermissionFromRole(String roleName, String permissionName) {
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        Permission permission = permissionRepository.findById(permissionName)
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_EXISTED));

        // Kiểm tra xem role có permission này không
        if (!role.getPermissions().contains(permission)) {
            throw new AppException(ErrorCode.INVALID_DATA, "Role does not have permission: " + permissionName);
        }

        role.getPermissions().remove(permission);
        roleRepository.save(role);

    }

    @Transactional
    public void deleteRole(String roleName) {
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        roleRepository.deleteById(roleName);
    }


}
