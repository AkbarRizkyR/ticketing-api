package io.github.akbarrizky.service.menu;

import io.github.akbarrizky.entity.menu.Menu;
import io.github.akbarrizky.repository.menu.MenuRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class MenuService {

    @Inject
    MenuRepository menuRepository;

    public List<Menu> getAllMenus() {
        return menuRepository.listAllOrdered();
    }
}
