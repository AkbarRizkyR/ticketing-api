package io.github.akbarrizky.repository.user;

import io.github.akbarrizky.entity.user.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, Long> {
}
