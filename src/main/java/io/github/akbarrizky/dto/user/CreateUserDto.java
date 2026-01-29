package io.github.akbarrizky.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class CreateUserDto {

    @NotBlank(message = "Nama wajib diisi")
    public String fullName;

    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    public String email;

    @NotBlank(message = "Password wajib diisi")
    public String password;

    public Set<Long> roleIds;
}
