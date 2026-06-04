package com.footballplatform.app.service;

import com.footballplatform.app.dto.UserDTO;
import java.util.List;

public interface UserService {

    List<UserDTO> findAll();

    UserDTO findById(Long id);

    UserDTO create(UserDTO dto);

    UserDTO update(UserDTO dto);

    void delete(Long id);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
