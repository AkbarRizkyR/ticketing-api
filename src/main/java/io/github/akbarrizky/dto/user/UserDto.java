package io.github.akbarrizky.dto.user;

import java.util.Set;

public class UserDto {
    public Long id;
    public String fullName;
    public String email;
    public Boolean isActive;
    public Set<String> roles;
}
