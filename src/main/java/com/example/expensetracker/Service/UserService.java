package com.example.expensetracker.Service;

import com.example.expensetracker.Payload.UserRequestDTO;
import com.example.expensetracker.Payload.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserById(Long userId);

    UserResponseDTO deleteUser(Long userId);

    UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO);
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO getUserByName(String name);
}
