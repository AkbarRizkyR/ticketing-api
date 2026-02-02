package io.github.akbarrizky.repository.tiket;

import io.github.akbarrizky.entity.tiket.TicketStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketStatusRepository
        implements PanacheRepository<TicketStatus> {
}
