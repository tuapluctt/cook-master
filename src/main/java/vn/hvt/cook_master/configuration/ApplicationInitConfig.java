package vn.hvt.cook_master.configuration;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.hvt.cook_master.constant.PredefinedRole;
import vn.hvt.cook_master.entity.Role;
import vn.hvt.cook_master.entity.User;
import vn.hvt.cook_master.repository.RoleRepository;
import vn.hvt.cook_master.repository.UserRepository;


import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";
    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    RoleRepository roleRepository;

    @Bean
    ApplicationRunner init(UserRepository userRepository) {
        return args -> {
            if(!userRepository.existsByUsername("admin")) {

                roleRepository.findById(PredefinedRole.ADMIN_ROLE).ifPresentOrElse(
                        role -> log.info("ADMIN role already exists"),
                        () -> roleRepository.save(Role.builder()
                                .roleName(PredefinedRole.ADMIN_ROLE)
                                .description("User role")
                                .build())
                );



                var roles = new HashSet<Role>();
                roles.add(roleRepository.findById(PredefinedRole.ADMIN_ROLE).get());

                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .fullName(ADMIN_USER_NAME)
                        .passwordHash(passwordEncoder.encode(ADMIN_PASSWORD))
                        .email("admin@gmail.com")
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("admin user has been created with default password : admin , please change it after login");
            }
            log.info("Application initialization completed .....");
        };
    }
}
