package io.github.akbarrizky.service.user;

import io.github.akbarrizky.dto.user.CreateUserDto;
import io.github.akbarrizky.dto.user.UserDto;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.exception.BadRequestException;
import io.github.akbarrizky.exception.NotFoundException;
import io.github.akbarrizky.repository.user.RoleRepository;
import io.github.akbarrizky.repository.user.UserRepository;
import io.github.akbarrizky.util.PasswordUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashSet;
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

        if (dto.roleIds != null) {
            user.roles = new HashSet<>(); // Initialize the set before adding roles
            for (Long roleId : dto.roleIds) {
                io.github.akbarrizky.entity.user.Role role = roleRepo.findByIdOptional(roleId)
                        .orElseThrow(() -> new NotFoundException("Role not found"));
                user.roles.add(role);
            }
        }

        userRepo.persist(user);

        return toDto(user);
    }

    public UserDto getById(java.util.UUID id) {
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
