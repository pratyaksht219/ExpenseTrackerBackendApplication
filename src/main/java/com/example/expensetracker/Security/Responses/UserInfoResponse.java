package com.example.expensetracker.Security.Responses;

import com.example.expensetracker.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long Id;
    private String username;
    private String email;
    private ResponseCookie cookie;
    private List<String> roles;
}
