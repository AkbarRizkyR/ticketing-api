package io.github.akbarrizky.repository.menu;

import io.github.akbarrizky.entity.menu.Menu;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MenuRepository implements PanacheRepositoryBase<Menu, UUID> {

    public List<Menu> listAllOrdered() {
        return list("order by order");
    }
}
