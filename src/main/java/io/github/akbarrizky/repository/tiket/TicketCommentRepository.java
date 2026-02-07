package io.github.akbarrizky.repository.tiket;

import io.github.akbarrizky.entity.tiket.TicketComment;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TicketCommentRepository implements PanacheRepositoryBase<TicketComment, UUID> {

    public List<TicketComment> findByTicketId(UUID ticketId) {
        return list("ticket.id", ticketId);
    }
}
