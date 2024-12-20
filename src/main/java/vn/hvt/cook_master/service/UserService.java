package vn.hvt.cook_master.service;

import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.dto.request.UserCreateRequest;
import vn.hvt.cook_master.dto.request.UserUpdateRequest;
import vn.hvt.cook_master.dto.response.UserResponse;

import java.io.IOException;

public interface UserService {
    UserResponse registerUser(UserCreateRequest userRequest);
    void updateProfile(String userId, UserUpdateRequest userUpdateRequest, MultipartFile file) throws IOException;
}
