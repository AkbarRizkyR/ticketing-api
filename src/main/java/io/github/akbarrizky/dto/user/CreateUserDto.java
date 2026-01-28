package io.github.akbarrizky.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class CreateUserDto {

    @NotBlank
    public String fullName;

    @Email
    public String email;

    @NotBlank
    public String password;

    public Set<Long> roleIds;
}
