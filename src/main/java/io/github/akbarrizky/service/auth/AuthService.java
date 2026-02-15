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

    @Inject
    io.github.akbarrizky.repository.auth.UserSessionRepository userSessionRepository;

    public String generateToken(String userId, String email, String fullName, Set<String> roles, Long tokenVersion,
            String sessionId) {
        return Jwt.issuer("ticketing-api")
                .subject(userId)
                .claim("userId", userId)
                .claim("fullName", fullName)
                .claim("tokenVersion", tokenVersion)
                .claim("sessionId", sessionId)
                .upn(email)
                .groups(roles)
                .expiresIn(Duration.ofHours(2))
                .sign();
    }

    @Inject
    UserRepository userRepository;

    @Inject
    JwtUtil jwtUtil;

    @jakarta.transaction.Transactional
    public String login(LoginDto dto, String ipAddress, String userAgent) {

        User user = userRepository.findByEmail(dto.email)
                .orElseThrow(() -> new UnauthorizedException("Email atau password salah"));

        if (!PasswordUtil.verify(dto.password, user.passwordHash)) {
            throw new UnauthorizedException("Email atau password salah");
        }

        // Create Session
        io.github.akbarrizky.entity.auth.UserSession session = new io.github.akbarrizky.entity.auth.UserSession();
        session.user = user;
        session.createdAt = java.time.Instant.now();
        session.expiresAt = session.createdAt.plus(Duration.ofHours(2));
        session.isActive = true;
        session.ipAddress = ipAddress;
        session.userAgent = userAgent;
        // Assuming single role for session or taking one. If multiple, maybe comma
        // separated or just primary.
        // For now, taking the first role name or empty.
        session.role = user.roles.isEmpty() ? "" : user.roles.iterator().next().name;

        userSessionRepository.persist(session);

        return generateToken(user.id.toString(), user.email, user.fullName,
                user.roles.stream().map(r -> r.name).collect(java.util.stream.Collectors.toSet()),
                user.tokenVersion == null ? 0L : user.tokenVersion,
                session.id.toString());
    }

    @jakarta.transaction.Transactional
    public void logout(String sessionId) {
        if (sessionId != null) {
            io.github.akbarrizky.entity.auth.UserSession session = userSessionRepository
                    .findById(java.util.UUID.fromString(sessionId));
            if (session != null) {
                session.isActive = false;
                userSessionRepository.persist(session);
            }
        }
    }
}
