package vn.hvt.cook_master.service;

import org.springframework.stereotype.Service;
import vn.hvt.cook_master.dto.request.PermissionRequest;
import vn.hvt.cook_master.dto.response.PermissionResponse;
import vn.hvt.cook_master.entity.Permission;

import java.util.List;
import java.util.Optional;

@Service
public interface PermissionService {
    List<PermissionResponse> getAllPermissions();
    PermissionResponse createPermission(PermissionRequest request);
    void deletePermission(String permissionName);
}
