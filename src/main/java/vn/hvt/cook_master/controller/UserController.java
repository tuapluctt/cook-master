package vn.hvt.cook_master.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.dto.request.UserCreateRequest;
import vn.hvt.cook_master.dto.request.UserUpdateRequest;
import vn.hvt.cook_master.dto.response.ApiResponse;
import vn.hvt.cook_master.dto.response.UserEditResponse;
import vn.hvt.cook_master.dto.response.UserResponse;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @GetMapping()
    public ApiResponse<List<UserResponse>> getAllUsers() {

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @PostMapping("/register")
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.registerUser(request))
                .build();
    }

    @PutMapping("/{userId}/role/add")
    public ApiResponse<UserResponse> getEditProfileForm(@PathVariable String userId,
                                                         @RequestParam String roleName) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.addRoleToUser(userId, roleName))
                .build();
    }

    @PutMapping(value = "/{userId}")
    public ApiResponse<?> updateProfile(
            @PathVariable String userId,
            @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest) throws IOException {


            userService.updateProfile(userId, userUpdateRequest);
            return ApiResponse.builder()
                    .result("Update profile successfully")
                    .build();

    }

}
