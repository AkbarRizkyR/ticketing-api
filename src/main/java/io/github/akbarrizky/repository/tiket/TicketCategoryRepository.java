package io.github.akbarrizky.repository.tiket;

import java.util.UUID;

import io.github.akbarrizky.entity.tiket.TicketCategory;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TicketCategoryRepository implements PanacheRepositoryBase<TicketCategory, UUID> {
}
