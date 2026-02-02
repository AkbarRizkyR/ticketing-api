package io.github.akbarrizky.util;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class JwtUtil {

    public String generateToken(Long id, String email, Set<String> roles) {

        return Jwt.issuer("ticketing-api")
                .upn(email)
                .claim("userId", id)
                .groups(roles)
                .expiresIn(3600)
                .sign();
    }
}
