package vn.hvt.cook_master.service.Impl;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hvt.cook_master.dto.request.RoleRequest;
import vn.hvt.cook_master.dto.response.RoleResponse;
import vn.hvt.cook_master.entity.Permission;
import vn.hvt.cook_master.entity.Role;
import vn.hvt.cook_master.repository.PermissionRepository;
import vn.hvt.cook_master.repository.RoleRepository;
import vn.hvt.cook_master.service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final PermissionRepository permissionRepository;
    RoleRepository roleRepository;

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RoleResponse> getRoleByName(String roleName) {
        return roleRepository.findById(roleName)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest roleRequest) {
        List<Permission> permissions = permissionRepository.findAllById(roleRequest.getPermissions());

        Role role = Role.builder()
                .roleName(roleRequest.getRoleName())
                .description(roleRequest.getDescription())
                .permissions(new HashSet<>(permissions)) // Set permissions cho role
                .build();

        Role savedRole = roleRepository.save(role);
        return convertToResponse(savedRole);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(String roleName, RoleRequest roleRequest) {
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found")); // Xử lý exception phù hợp
        role.setDescription(roleRequest.getDescription());
        // Cập nhật các trường khác nếu cần
        Role updatedRole = roleRepository.save(role);
        return convertToResponse(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(String roleName) {
        roleRepository.deleteById(roleName);
    }

    private RoleResponse convertToResponse(Role role) {
        return RoleResponse.builder()
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build();
    }

    private RoleResponse convertToResponse(RoleRequest request) {
        return RoleResponse.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .build();
    }
}
