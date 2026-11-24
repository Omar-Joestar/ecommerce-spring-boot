package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.UserDTO;
import com.example.ecommerce.user.entity.UserEntity;
import com.example.ecommerce.user.mapper.UserMapper;
import com.example.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void checkEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public String  encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword,encodedPassword);
    }

    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->
                new IllegalArgumentException("User Not Found")
        );
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("User Not Found"));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserDTO updateUserProfile(Long id, String firstName, String lastName) {
        UserEntity user = getUserById(id);

        user.setFirstName(firstName);
        user.setLastName(lastName);

        UserEntity updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }

    public UserDTO getUserDTOById(Long id) {
        UserEntity user = getUserById(id);
        return userMapper.toDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO) {

        checkEmailAvailability(userDTO.getEmail());

        UserEntity user = userMapper.toEntity(userDTO);

        user.setPassword(encodePassword(userDTO.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }





}
