package org.wierzbadev.userservice.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wierzbadev.userservice.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(@NotBlank(message = "User's email must not be empty") String email);

    boolean existsByEmail(@NotBlank(message = "User's email must not be empty") String email);

    User findUserByUuid(UUID uuid);
}
