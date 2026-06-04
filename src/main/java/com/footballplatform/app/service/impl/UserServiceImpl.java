package com.footballplatform.app.service.impl;

import com.footballplatform.app.dto.UserDTO;
import com.footballplatform.app.entity.User;
import com.footballplatform.app.repository.UserRepository;
import com.footballplatform.app.service.UserService;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        return toDto(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id)));
    }

    @Override
    public UserDTO create(UserDTO dto) {
        if (existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists.");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required.");
        }

        User user = new User();
        applyDto(user, dto, true);
        return toDto(userRepository.save(user));
    }

    @Override
    public UserDTO update(UserDTO dto) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getId()));

        if (existsByUsernameAndIdNot(dto.getUsername(), dto.getId())) {
            throw new RuntimeException("Username already exists.");
        }

        applyDto(user, dto, false);
        return toDto(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return userRepository.findByUsername(username)
                .map(existing -> !existing.getId().equals(id))
                .orElse(false);
    }

    private void applyDto(User user, UserDTO dto, boolean createMode) {
        user.setUsername(dto.getUsername().trim());
        user.setEnabled(dto.getEnabled());
        user.setRole(dto.getRole());

        String rawPassword = dto.getPassword() == null ? "" : dto.getPassword().trim();
        if (createMode || !rawPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
    }

    private UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .enabled(user.getEnabled())
                .role(user.getRole())
                .build();
    }
}
