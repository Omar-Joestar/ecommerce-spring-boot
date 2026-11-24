package com.example.ecommerce.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        }
)
public class UserEntity {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@NotNull
@Column(unique = true)
private String email;

@NotNull
private String password;

@NotNull
private String role;

@NotNull
@Column(name = "first_name")
private String firstName;

@NotNull
@Column(name = "last_name")
private String lastName;

@Column(nullable = false)
private Boolean enabled = true;

@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(name = "updated_at", nullable = false)
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}
@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}


}
