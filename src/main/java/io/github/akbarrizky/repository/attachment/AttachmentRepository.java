package io.github.akbarrizky.repository.attachment;

import io.github.akbarrizky.entity.attachment.AttachmentEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AttachmentRepository implements PanacheRepositoryBase<AttachmentEntity, UUID> {

    public List<AttachmentEntity> findByTicketId(UUID ticketId) {
        return list("ticketId", ticketId);
    }

}
