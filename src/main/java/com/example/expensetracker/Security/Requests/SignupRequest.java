package com.example.expensetracker.Security.Requests;

import com.example.expensetracker.Entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotBlank
    @Email
    @Size(max = 50, message = "Email must contain at most 50 characters")
    private String email;
    @NotBlank
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    @NotBlank
    private String password;
    private List<Role> roles;
}
