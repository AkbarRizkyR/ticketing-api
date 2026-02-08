package io.github.akbarrizky.entity.tiket;

import io.github.akbarrizky.entity.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Column(name = "ticket_code", length = 20, unique = true)
    public String ticketCode;

    @Column(length = 255)
    public String title;

    @Column(length = 2000)
    public String description;

    // RELASI KE MASTER
    @ManyToOne
    @JoinColumn(name = "priority_id")
    public TicketPriority priority;

    @Column(name = "priority")
    public String priorityName;

    @ManyToOne
    @JoinColumn(name = "status_id")
    public TicketStatus status;

    @Column(name = "status")
    public String statusName;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public TicketCategory category;

    @Column(name = "category")
    public String categoryName;

    // RELASI USER

    @Column(name = "created_by", length = 100)
    public String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    public User assignedTo;

    @Column(name = "assigned_name")
    public String assignedName;

    @ManyToOne
    @JoinColumn(name = "reported_id")
    public User reportedBy;

    @Column(name = "reported_name")
    public String reportedName;

}
