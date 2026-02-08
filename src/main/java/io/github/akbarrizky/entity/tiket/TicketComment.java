package io.github.akbarrizky.entity.tiket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import static jakarta.persistence.FetchType.EAGER;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.entity.attachment.AttachmentEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.UUID;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @ManyToOne
    @JoinColumn(name = "ticket_code", referencedColumnName = "ticket_code")
    public Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Column(columnDefinition = "TEXT")
    public String comment;

    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "comment_id") // This will create comment_id column in attachment table
    public List<AttachmentEntity> attachments;

    @CreationTimestamp
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    public LocalDateTime updatedAt;
}
