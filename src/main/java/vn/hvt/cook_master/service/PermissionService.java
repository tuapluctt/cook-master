package vn.hvt.cook_master.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.hvt.cook_master.dto.request.PermissionRequest;
import vn.hvt.cook_master.dto.response.PermissionResponse;
import vn.hvt.cook_master.entity.Permission;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.repository.PermissionRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService  {

    PermissionRepository permissionRepository;
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    public PermissionResponse createPermission(PermissionRequest permissionRequest) {
        if (permissionRepository.existsById(permissionRequest.getPermissionName())) {
            throw new AppException(ErrorCode.PERMISSION_EXISTED);
        }

        Permission permission = Permission.builder()
                .permissionName(permissionRequest.getPermissionName())
                .description(permissionRequest.getDescription())
                .build();

        Permission savedPermission = permissionRepository.save(permission);
        return convertToResponse(savedPermission);
    }

    public void deletePermission(String permissionName) {
        if (!permissionRepository.existsById(permissionName)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        permissionRepository.deleteById(permissionName);
    }

    private PermissionResponse convertToResponse(Permission permission) {
        return PermissionResponse.builder()
                .permissionName(permission.getPermissionName())
                .description(permission.getDescription())
                .build();
    }
}
