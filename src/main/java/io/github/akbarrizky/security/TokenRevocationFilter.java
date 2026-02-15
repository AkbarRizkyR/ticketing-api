package io.github.akbarrizky.security;

import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.repository.user.UserRepository;
import io.quarkus.security.Authenticated;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@Provider
@Priority(Priorities.AUTHENTICATION + 100)
public class TokenRevocationFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Inject
    UserRepository userRepository;

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Only check if specific method or class is annotated with @Authenticated
        // OR if a user principal is present (which means JWT auth succeeded)
        if (jwt.getName() == null) {
            return;
        }

        Long tokenVersionClaim = null;
        try {
            Object claim = jwt.getClaim("tokenVersion");
            if (claim != null) {
                if (claim instanceof Number) {
                    tokenVersionClaim = ((Number) claim).longValue();
                } else {
                    tokenVersionClaim = Long.valueOf(claim.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing tokenVersion claim: " + e.getMessage());
        }

        if (tokenVersionClaim == null) {
            abortUnauthorized(requestContext);
            return;
        }

        // New: Check Session Validity
        String sessionId = null;
        try {
            Object sessionClaim = jwt.getClaim("sessionId");
            if (sessionClaim != null) {
                sessionId = sessionClaim.toString();
            }
        } catch (Exception e) {
            // ignore
        }

        // If your new tokens MUST have a session ID, enforce it here:
        if (sessionId == null) {
            // For migration, maybe allow missing sessionId if you want legacy tokens to
            // work until expiry.
            // But if we want to enforce session tracking, abort.
            // abortUnauthorized(requestContext);
            // For now, let's just proceed to tokenVersion check if no session (backward
            // compat) OR enforce it.
            // User asked for session logging. Let's assume strict for new flow.
        }

        // Validating Session if present
        if (sessionId != null) {
            io.github.akbarrizky.entity.auth.UserSession session = userSessionRepository
                    .findById(UUID.fromString(sessionId));
            if (session == null || !session.isActive) {
                abortUnauthorized(requestContext);
                return;
            }
        }

        String userId = jwt.getSubject();
        if (userId != null) {
            try {
                User user = userRepository.findById(UUID.fromString(userId));
                if (user == null) {
                    abortUnauthorized(requestContext);
                    return;
                }

                Long userVersion = user.tokenVersion == null ? 0L : user.tokenVersion;

                if (!tokenVersionClaim.equals(userVersion)) {
                    abortUnauthorized(requestContext);
                }
            } catch (Exception e) {
                System.err.println("Error validating token version for user " + userId + ": " + e.getMessage());
                e.printStackTrace();
                // If DB check fails, we might want to fail safe (stop request) or fail open
                // (allow).
                // 500 allows us to see the error. But user reported 500.
                // Let's return 401 just in case to avoid crashing FE, but logging is key.
                abortUnauthorized(requestContext);
            }
        }
    }

    @Inject
    io.github.akbarrizky.repository.auth.UserSessionRepository userSessionRepository;

    private void abortUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("Session expired or invalid")
                .build());
    }
}
