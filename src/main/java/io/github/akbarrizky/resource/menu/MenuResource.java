package io.github.akbarrizky.resource.menu;

import io.github.akbarrizky.entity.menu.Menu;
import io.github.akbarrizky.service.menu.MenuService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/menus")
@Produces(MediaType.APPLICATION_JSON)
public class MenuResource {

    @Inject
    MenuService menuService;

    @GET
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }
}
