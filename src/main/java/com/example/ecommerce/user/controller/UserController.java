package com.example.ecommerce.user.controller;

import com.example.ecommerce.user.dto.UserDTO;
import com.example.ecommerce.user.entity.UserEntity;
import com.example.ecommerce.user.mapper.UserMapper;
import com.example.ecommerce.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    @GetMapping("/{id}")
    public UserDTO GetUserDTOByID( @PathVariable long id){
        return userService.getUserDTOById(id);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@RequestBody UserDTO userDTO, @PathVariable long id) {

        return userService.updateUserProfile(
                id,
                userDTO.getFirstName(),
                userDTO.getLastName()
        );

    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }


}
