package io.github.akbarrizky.entity.menu;

import jakarta.persistence.*;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public java.util.UUID id;

    @Column(nullable = false)
    public String label;

    @Column(nullable = false)
    public String path;

    public String icon;

    @Column(name = "order_menu")
    public Integer order;
}
