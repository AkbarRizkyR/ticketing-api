package io.github.akbarrizky.repository.tiket;

import io.github.akbarrizky.entity.tiket.TicketPriority;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketPriorityRepository
        implements PanacheRepository<TicketPriority> {
}
