package com.lucamoretti.adventure_together.dto.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// DTO per la richiesta di reset della password

@Data
public class PasswordResetRequestDTO {
    @NotBlank
    @Email
    private String email;
}
