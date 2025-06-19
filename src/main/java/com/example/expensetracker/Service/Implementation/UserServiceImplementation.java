package com.example.expensetracker.Service.Implementation;

import com.example.expensetracker.Entity.User;
import com.example.expensetracker.Payload.UserRequestDTO;
import com.example.expensetracker.Payload.UserResponseDTO;
import com.example.expensetracker.Repository.CategoryRepository;
import com.example.expensetracker.Repository.UserRepository;
import com.example.expensetracker.Service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User newUser = new User();


        newUser.setName(userRequestDTO.getName());
        newUser.setEmail(userRequestDTO.getEmail());
        newUser.setPassword((userRequestDTO.getPassword()));
        userRepository.save(newUser);

        return modelMapper.map(newUser, UserResponseDTO.class);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new RuntimeException("No users found");
        }
        return users.stream()
                .map(user-> modelMapper.map(user, UserResponseDTO.class))
                .toList();
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return modelMapper.map(targetUser, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO) {
        return null;
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        User targetUser = userRepository.findByEmail(email);
        if(targetUser == null){
            throw new RuntimeException("User not found");
        }
        return modelMapper.map(targetUser, UserResponseDTO.class);

    }

    @Override
    public UserResponseDTO getUserByName(String name) {
        User targetUser = userRepository.findByName(name);
        if(targetUser == null){
            throw new RuntimeException("User not found");
        }
        return modelMapper.map(targetUser, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO deleteUser(Long userId) {
        User deletedUser = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        userRepository.delete(deletedUser);

        return modelMapper.map(deletedUser, UserResponseDTO.class);
    }
}
