package io.github.akbarrizky.service.auth;

import io.github.akbarrizky.dto.auth.LoginDto;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.exception.UnauthorizedException;
import io.github.akbarrizky.repository.user.UserRepository;
import io.github.akbarrizky.util.JwtUtil;
import io.github.akbarrizky.util.PasswordUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    public String generateToken(String userId, String email, String fullName, Set<String> roles) {
        return Jwt.issuer("ticketing-api")
                .subject(userId)
                .claim("userId", userId)
                .claim("fullName", fullName)
                .upn(email)
                .groups(roles)
                .expiresIn(Duration.ofHours(2))
                .sign();
    }

    @Inject
    UserRepository userRepository;

    @Inject
    JwtUtil jwtUtil;

    public String login(LoginDto dto) {

        User user = userRepository.findByEmail(dto.email)
                .orElseThrow(() -> new UnauthorizedException("Email atau password salah"));

        if (!PasswordUtil.verify(dto.password, user.passwordHash)) {
            throw new UnauthorizedException("Email atau password salah");
        }

        return generateToken(user.id.toString(), user.email, user.fullName,
                user.roles.stream().map(r -> r.name).collect(java.util.stream.Collectors.toSet()));
    }
}
