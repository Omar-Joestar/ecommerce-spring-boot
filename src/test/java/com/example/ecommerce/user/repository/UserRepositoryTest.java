
package com.example.ecommerce.user.repository;

import com.example.ecommerce.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("ecommerce_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUserSuccessfully() {

        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("CUSTOMER");
        user.setEnabled(true);

        // Act
        UserEntity savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals("CUSTOMER", savedUser.getRole());
        assertTrue(savedUser.getEnabled());
    }

    @Test
    void findUserByEmailSuccessfully() {

        // Arrange
        UserEntity user = new UserEntity();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole("CUSTOMER");
        user.setEnabled(true);

        userRepository.save(user);

        // Act
        UserEntity foundUser = userRepository.findByEmail("john@example.com")
                .orElseThrow();

        // Assert
        assertNotNull(foundUser);
        assertEquals("john@example.com", foundUser.getEmail());
        assertEquals("John", foundUser.getFirstName());
    }

    @Test
    void findUserByEmailReturnsEmptyWhenNotFound() {

        // Act
        var result = userRepository.findByEmail("unknown@example.com");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmailReturnsFalseWhenEmailDoesNotExist() {

        // Act
        boolean result = userRepository.existsByEmail("unknown@example.com");

        // Assert
        assertFalse(result);


    }


}

