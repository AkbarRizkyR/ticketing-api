package io.github.akbarrizky.entity.tiket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_priorities")
public class TicketPriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(length = 50)
    public String name;

    @Column(columnDefinition = "text")
    public String description;

    @Column(name = "level_priority")
    public Integer level;
}