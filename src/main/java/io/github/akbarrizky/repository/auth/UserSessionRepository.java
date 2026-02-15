package io.github.akbarrizky.repository.auth;

import io.github.akbarrizky.entity.auth.UserSession;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UserSessionRepository implements PanacheRepositoryBase<UserSession, UUID> {
}
