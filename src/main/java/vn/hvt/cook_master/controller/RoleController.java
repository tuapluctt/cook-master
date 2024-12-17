package vn.hvt.cook_master.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.hvt.cook_master.dto.request.RoleRequest;
import vn.hvt.cook_master.dto.response.ApiResponse;
import vn.hvt.cook_master.dto.response.RoleResponse;
import vn.hvt.cook_master.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @GetMapping("/{roleName}")
    public ApiResponse<RoleResponse> getRoleByName(@PathVariable String roleName) {
        return roleService.getRoleByName(roleName)
                .map(role -> ApiResponse.<RoleResponse>builder()
                        .result(role)
                        .build())
                .orElse(ApiResponse.<RoleResponse>builder()
                        .message("Role not found")
                        .build());
    }

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(roleRequest))
                .build();
    }

    @PutMapping("/{roleName}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable String roleName, @Valid @RequestBody RoleRequest roleRequest) {
        if (roleService.getRoleByName(roleName).isEmpty()) {
            return ApiResponse.<RoleResponse>builder()
                    .message("Role not found")
                    .build();
        }
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRole(roleName, roleRequest))
                .build();
    }

    @DeleteMapping("/{roleName}")
    public ApiResponse<?> deleteRole(@PathVariable String roleName) {
        if (roleService.getRoleByName(roleName).isEmpty()) {
            return ApiResponse.builder()
                    .message("Role not found")
                    .build();
        }
        roleService.deleteRole(roleName);
        return ApiResponse.builder()
                .build();
    }
}
