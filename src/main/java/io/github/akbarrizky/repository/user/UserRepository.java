package io.github.akbarrizky.repository.user;

import io.github.akbarrizky.entity.user.User;
import java.util.List;
import java.util.Optional;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public boolean existsByEmail(String email) {
        return find("email", email).firstResult() != null;
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public List<User> searchByEmailOrName(String keyword) {
        return list("email like ?1 or name like ?1",
                "%" + keyword + "%");
    }
}
