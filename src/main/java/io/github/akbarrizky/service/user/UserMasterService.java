package io.github.akbarrizky.service.user;

import io.github.akbarrizky.dto.master.UserMasterDto;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.repository.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserMasterService {

    @Inject
    UserRepository userRepository;

    public List<UserMasterDto> getAll() {
        return userRepository.listAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserMasterDto getById(Long id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new RuntimeException("User tidak ditemukan");
        }

        return toDto(user);
    }

    public List<UserMasterDto> search(String keyword) {
        return userRepository.searchByEmailOrName(keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private UserMasterDto toDto(User user) {
        return new UserMasterDto(
                user.id,
                user.email,
                user.fullName);
    }
}
