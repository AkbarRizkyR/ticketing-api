package io.github.akbarrizky.repository.tiket;

import io.github.akbarrizky.entity.tiket.Ticket;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class TicketRepository implements PanacheRepositoryBase<Ticket, UUID> {
}
