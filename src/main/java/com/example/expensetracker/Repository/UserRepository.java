package com.example.expensetracker.Repository;

import com.example.expensetracker.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String username);

    boolean existsByName(String user1);

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.name = ?1")
    Optional<User> findByUserName(String username);

    boolean existsByEmail(@NotBlank @Email @Size(max = 50, message = "Email must contain at most 50 characters") String email);
}
