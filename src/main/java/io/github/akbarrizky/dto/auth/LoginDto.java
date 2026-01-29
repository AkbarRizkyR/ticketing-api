package io.github.akbarrizky.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {

    @NotBlank(message = "Email wajib diisi")
    public String email;

    @NotBlank(message = "Password wajib diisi")
    public String password;
}
