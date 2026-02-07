package io.github.akbarrizky.entity.tiket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import io.github.akbarrizky.entity.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.util.UUID;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    public Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Column(columnDefinition = "TEXT")
    public String comment;

    @CreationTimestamp
    public LocalDateTime createdAt;
}
