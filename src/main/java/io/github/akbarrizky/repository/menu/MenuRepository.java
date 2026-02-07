package io.github.akbarrizky.repository.menu;

import io.github.akbarrizky.entity.menu.Menu;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MenuRepository implements PanacheRepository<Menu> {

    public List<Menu> listAllOrdered() {
        return list("order by order");
    }
}
