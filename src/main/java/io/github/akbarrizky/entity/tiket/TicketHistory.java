package io.github.akbarrizky.entity.tiket;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_history")
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @ManyToOne
    @JoinColumn(name = "ticket_code", referencedColumnName = "ticket_code")
    public Ticket ticket;

    @Column(nullable = false)
    public String action; // CREATED, UPDATED

    public String field; // status, priority, assigned_to, etc.

    @Column(name = "old_value", columnDefinition = "TEXT")
    public String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    public String newValue;

    @Column(name = "changed_by")
    public String changedBy; // User Name or ID

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    public LocalDateTime changedAt;
}
