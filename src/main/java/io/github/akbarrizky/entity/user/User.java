package io.github.akbarrizky.entity.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public java.util.UUID id;

    @Column(name = "full_name", nullable = false)
    public String fullName;

    @Column(unique = true, nullable = false)
    public String email;

    @Column(name = "password_hash", nullable = false)
    public String passwordHash;

    @Column(name = "is_active")
    public Boolean isActive = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    public Set<Role> roles;

    @Column(name = "created_at")
    public Instant createdAt = Instant.now();

    @Column(name = "token_version")
    public Long tokenVersion = 0L;
}
