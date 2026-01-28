package io.github.akbarrizky.service.user;

import io.github.akbarrizky.dto.user.CreateUserDto;
import io.github.akbarrizky.dto.user.UserDto;
import io.github.akbarrizky.entity.user.Role;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.exception.BadRequestException;
import io.github.akbarrizky.exception.NotFoundException;
import io.github.akbarrizky.repository.user.RoleRepository;
import io.github.akbarrizky.repository.user.UserRepository;
import io.github.akbarrizky.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepo;

    @Inject
    RoleRepository roleRepo;

    @Transactional
    public UserDto create(CreateUserDto dto) {

        if (userRepo.existsByEmail(dto.email)) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.fullName = dto.fullName;
        user.email = dto.email;
        user.passwordHash = PasswordUtil.hash(dto.password);
        user.isActive = true;

        if (dto.roleIds != null && !dto.roleIds.isEmpty()) {
            Set<Role> roles = dto.roleIds.stream()
                    .map(id -> roleRepo.findByIdOptional(id)
                            .orElseThrow(() -> new NotFoundException("Role not found: " + id)))
                    .collect(Collectors.toSet());
            user.roles = roles;
        }

        userRepo.persist(user);

        return toDto(user);
    }

    public UserDto getById(Long id) {
        User user = userRepo.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return toDto(user);
    }

    private UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.id = user.id;
        dto.fullName = user.fullName;
        dto.email = user.email;
        dto.isActive = user.isActive;
        dto.roles = user.roles == null ? Set.of() : user.roles.stream().map(r -> r.name).collect(Collectors.toSet());
        return dto;
    }
}
