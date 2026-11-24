
package com.example.ecommerce.user.service;

import com.example.ecommerce.user.dto.UserDTO;
import com.example.ecommerce.user.entity.UserEntity;
import com.example.ecommerce.user.mapper.UserMapper;
import com.example.ecommerce.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserSuccessfully() {

        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(userDTO))
                .thenReturn(userEntity);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(userEntity))
                .thenReturn(userEntity);

        when(userMapper.toDTO(userEntity))
                .thenReturn(expectedDTO);

        // Act
        UserDTO result = userService.createUser(userDTO);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", userEntity.getPassword());

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(userEntity);
    }


    @Test
    void createUserWithExistingEmailThrowsException() {

        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(userDTO)
        );

        verify(userRepository).existsByEmail("test@example.com");

        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }
    @Test
    void encodePasswordSuccessfully() {

        // Arrange
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        // Act
        String result = userService.encodePassword(rawPassword);

        // Assert
        assertEquals(encodedPassword, result);

        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void checkPasswordSuccessfully() {

        // Arrange
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        // Act
        boolean result = userService.checkPassword(rawPassword, encodedPassword);

        // Assert
        assertTrue(result);

        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
    @Test
    void getUserByIdSuccessfully() {

        // Arrange
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@example.com");

        when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.of(userEntity));

        // Act
        UserEntity result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findById(userId);
    }
    @Test
    void getUserByIdThrowsExceptionWhenNotFound() {

        // Arrange
        Long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("User Not Found", exception.getMessage());

        verify(userRepository).findById(userId);
    }
    @Test
    void getUserByEmailSuccessfully() {

        // Arrange
        String email = "test@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(email);

        when(userRepository.findByEmail(email))
                .thenReturn(java.util.Optional.of(userEntity));

        // Act
        UserEntity result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(1L, result.getId());

        verify(userRepository).findByEmail(email);
    }
    @Test
    void getUserByEmailThrowsExceptionWhenNotFound() {

        // Arrange
        String email = "unknown@example.com";

        when(userRepository.findByEmail(email))
                .thenReturn(java.util.Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserByEmail(email)
        );

        assertEquals("User Not Found", exception.getMessage());

        verify(userRepository).findByEmail(email);
    }
    @Test
    void getUserDTOByIdSuccessfully() {

        // Arrange
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setEmail("test@example.com");

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setId(userId);
        expectedDTO.setEmail("test@example.com");

        when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.of(userEntity));

        when(userMapper.toDTO(userEntity))
                .thenReturn(expectedDTO);

        // Act
        UserDTO result = userService.getUserDTOById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(userMapper).toDTO(userEntity);
    }
    @Test
    void getAllUsersSuccessfully() {

        // Arrange
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setEmail("john@example.com");

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setEmail("jane@example.com");

        UserDTO dto1 = new UserDTO();
        dto1.setId(1L);
        dto1.setEmail("john@example.com");

        UserDTO dto2 = new UserDTO();
        dto2.setId(2L);
        dto2.setEmail("jane@example.com");

        when(userRepository.findAll())
                .thenReturn(java.util.List.of(user1, user2));

        when(userMapper.toDTO(user1)).thenReturn(dto1);
        when(userMapper.toDTO(user2)).thenReturn(dto2);

        // Act
        java.util.List<UserDTO> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertEquals("john@example.com", result.get(0).getEmail());
        assertEquals("jane@example.com", result.get(1).getEmail());

        verify(userRepository).findAll();
        verify(userMapper).toDTO(user1);
        verify(userMapper).toDTO(user2);
    }
    @Test
    void updateUserProfileSuccessfully() {

        // Arrange
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");

        UserEntity updatedEntity = new UserEntity();
        updatedEntity.setId(userId);
        updatedEntity.setFirstName("Jonathan");
        updatedEntity.setLastName("Smith");

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setId(userId);
        expectedDTO.setFirstName("Jonathan");
        expectedDTO.setLastName("Smith");

        when(userRepository.findById(userId))
                .thenReturn(java.util.Optional.of(userEntity));

        when(userRepository.save(userEntity))
                .thenReturn(updatedEntity);

        when(userMapper.toDTO(updatedEntity))
                .thenReturn(expectedDTO);

        // Act
        UserDTO result = userService.updateUserProfile(
                userId, "Jonathan", "Smith"
        );

        // Assert
        assertNotNull(result);
        assertEquals("Jonathan", result.getFirstName());
        assertEquals("Smith", result.getLastName());

        verify(userRepository).findById(userId);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDTO(updatedEntity);
    }
}

