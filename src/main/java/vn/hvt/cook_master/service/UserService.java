package vn.hvt.cook_master.service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.hvt.cook_master.constant.PredefinedRole;
import vn.hvt.cook_master.dto.request.UserCreateRequest;
import vn.hvt.cook_master.dto.request.UserUpdateRequest;
import vn.hvt.cook_master.dto.response.UserEditResponse;
import vn.hvt.cook_master.dto.response.UserResponse;
import vn.hvt.cook_master.entity.Role;
import vn.hvt.cook_master.entity.User;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.mapper.UserMapper;
import vn.hvt.cook_master.repository.RoleRepository;
import vn.hvt.cook_master.repository.UserRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    RoleRepository roleRepository;
    UserMapper userMapper;
    UserRepository userRepository;


    public UserResponse registerUser(UserCreateRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        var user = userMapper.toUser(userRequest);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // set pass
        user.setPasswordHash(passwordEncoder.encode(userRequest.getPasswordHash()));

        // Gán role mặc định là USER
        Role userRole = roleRepository.findById(PredefinedRole.USER_ROLE)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setRoles(roles);

        var savedUser = userRepository.save(user);
        // Xử lý nickname: @cook_ + id người dùng
        String nickname = "@cook_" + user.getUsername();

        savedUser.setNickName(nickname);

        return userMapper.toUserResponse( userRepository.save(savedUser));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }


    @PostAuthorize("returnObject.username == authentication.name")
    public void updateProfile(String userId, UserUpdateRequest userUpdateRequest) throws IOException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
            user.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getNickName() != null) {
            user.setNickName(userUpdateRequest.getNickName());
        }

        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserEditResponse getUserById(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserEditResponse(user);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

//        log.info("User {} is calling getAllUsers", auth.getName());
//        auth.getAuthorities()
//                .forEach(grantedAuthority -> log.info("User has role {}", grantedAuthority.getAuthority()));

        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse addRoleToUser(String userId, String roleName) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var role = roleRepository.findById(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED));

        if (user.getRoles().contains(role)) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }

        user.getRoles().add(role);
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
