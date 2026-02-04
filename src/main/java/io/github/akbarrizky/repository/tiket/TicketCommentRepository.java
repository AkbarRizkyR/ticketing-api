package io.github.akbarrizky.repository.tiket;

import io.github.akbarrizky.entity.tiket.TicketComment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TicketCommentRepository implements PanacheRepository<TicketComment> {

    public List<TicketComment> findByTicketId(Long ticketId) {
        return list("ticket.id", ticketId);
    }
}
