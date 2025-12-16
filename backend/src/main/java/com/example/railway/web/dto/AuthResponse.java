package com.example.railway.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Integer userId;
    private String username;
    private String role;

    public static AuthResponse fromUser(com.example.railway.domain.UserAccount user) {
        return new AuthResponse("mock-token-" + user.getId(), user.getId(), user.getName(), user.getRole().name());
    }
}
