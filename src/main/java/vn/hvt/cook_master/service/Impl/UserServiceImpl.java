package vn.hvt.cook_master.service.Impl;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.dto.request.UserCreateRequest;
import vn.hvt.cook_master.dto.request.UserUpdateRequest;
import vn.hvt.cook_master.dto.response.UserResponse;
import vn.hvt.cook_master.entity.Role;
import vn.hvt.cook_master.entity.User;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.mapper.UserMapper;
import vn.hvt.cook_master.repository.RoleRepository;
import vn.hvt.cook_master.repository.UserRepository;
import vn.hvt.cook_master.service.ImageService;
import vn.hvt.cook_master.service.UserService;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    RoleRepository roleRepository;
    UserMapper userMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    ImageService imageService;

    @Override
    public UserResponse registerUser(UserCreateRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        var user = userMapper.toUser(userRequest);
        // set pass
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));

        // Gán role mặc định là USER
        Role userRole = roleRepository.findById("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setRoles(roles);

        var savedUser = userRepository.save(user);
        // Xử lý nickname: @cook_ + id người dùng
        String nickname = "@cook_" + user.getUserId();

        savedUser.setNickName(nickname);

        return userMapper.toUserResponse( userRepository.save(savedUser));
    }

    @Override
    public void updateProfile(String userId, UserUpdateRequest userUpdateRequest, MultipartFile file) throws IOException {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));


        // Cập nhật thông tin cơ bản
        user.setFullName(userUpdateRequest.getFullName());
        user.setAddress(userUpdateRequest.getAddress());
        user.setDescription(userUpdateRequest.getDescription());

        // Xử lý upload ảnh nếu có
        if (file != null && !file.isEmpty()) {
            String imageUrl = imageService.uploadImage(file, "user", null, null, user.getUserId());
            user.setProfilePictureUrl(imageUrl);
        }

        userRepository.save(user);
    }
}
