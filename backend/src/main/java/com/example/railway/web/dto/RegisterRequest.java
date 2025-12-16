package com.example.railway.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;
    
    @NotBlank
    private String idNumber;
    
    @NotBlank
    private String phone;
    
    @NotBlank
    private String password;
    
    private String email;
}
