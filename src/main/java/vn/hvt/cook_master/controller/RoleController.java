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
@RequestMapping("/roles")
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


    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(roleRequest))
                .build();
    }

    @PostMapping("/{roleName}/permissions/add")
    public ApiResponse<?> addPermissionToRole(
            @PathVariable String roleName,
            @RequestParam String permissionName) {
       roleService.addPermissionToRole(roleName, permissionName);
        return ApiResponse.builder().build();
    }

    @DeleteMapping("/{roleName}/permissions/remove")
    public ApiResponse<?> removePermissionFromRole(
            @PathVariable String roleName,
            @RequestParam String permissionName) {
        roleService.removePermissionFromRole(roleName, permissionName);
        return ApiResponse.builder().build();
    }

    @DeleteMapping("/{roleName}")
    public ApiResponse<?> deleteRole(@PathVariable String roleName) {

        roleService.deleteRole(roleName);
        return ApiResponse.builder()
                .build();
    }
}
