package com.footballplatform.app.config;

import com.footballplatform.app.entity.User;
import com.footballplatform.app.entity.UserRole;
import com.footballplatform.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder,
                                           @Value("${app.security.default-admin.username:admin}") String username,
                                           @Value("${app.security.default-admin.password:admin123}") String password) {
        return args -> {
            if (userRepository.existsByUsername(username)) {
                return;
            }

            User adminUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .enabled(true)
                    .role(UserRole.ADMIN)
                    .build();
            userRepository.save(adminUser);
        };
    }
}
