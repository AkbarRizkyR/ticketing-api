package io.github.akbarrizky.entity.user;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role extends PanacheEntity {

    @Column(unique = true, nullable = false)
    public String name;

    public String description;
}
