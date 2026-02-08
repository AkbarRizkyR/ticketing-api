package io.github.akbarrizky.util;

import io.github.akbarrizky.entity.tiket.TicketCategory;
import io.github.akbarrizky.entity.tiket.TicketPriority;
import io.github.akbarrizky.entity.tiket.TicketStatus;
import io.github.akbarrizky.entity.user.Role;
import io.github.akbarrizky.entity.user.User;
import io.github.akbarrizky.repository.tiket.TicketCategoryRepository;
import io.github.akbarrizky.repository.tiket.TicketPriorityRepository;
import io.github.akbarrizky.repository.tiket.TicketStatusRepository;
import io.github.akbarrizky.repository.user.RoleRepository;
import io.github.akbarrizky.repository.user.UserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Set;

@ApplicationScoped
public class DataSeeder {

    private static final Logger LOG = Logger.getLogger(DataSeeder.class);

    @Inject
    RoleRepository roleRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TicketPriorityRepository priorityRepository;

    @Inject
    TicketStatusRepository statusRepository;

    @Inject
    TicketCategoryRepository categoryRepository;

    @Inject
    io.github.akbarrizky.repository.menu.MenuRepository menuRepository;

    @Inject
    jakarta.persistence.EntityManager em;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        LOG.info("Checking for seed data...");

        // 1. Seed Roles
        if (roleRepository.count() == 0) {
            seedRoles();
        }

        // 2. Seed Users (Admin, Devs, Support)
        seedUsers();

        // 3. Seed Master Data
        if (priorityRepository.count() == 0)
            seedPriorities();
        if (statusRepository.count() == 0)
            seedStatuses();
        if (categoryRepository.count() == 0)
            seedCategories();

        // 4. Seed Menus
        if (menuRepository.count() == 0)
            seedMenus();

        // 5. Enrich User Roles (Add full_name column)
        enrichUserRoles();
    }

    private void enrichUserRoles() {
        try {
            // Add column if not exists
            em.createNativeQuery("ALTER TABLE user_roles ADD COLUMN IF NOT EXISTS full_name VARCHAR(255)")
                    .executeUpdate();

            // Populate full_name from users table
            em.createNativeQuery(
                    "UPDATE user_roles SET full_name = (SELECT full_name FROM users WHERE users.id = user_roles.user_id)")
                    .executeUpdate();

            LOG.info("Enriched user_roles with full_name.");
        } catch (Exception e) {
            LOG.error("Failed to enrich user_roles: " + e.getMessage());
        }
    }

    private void seedRoles() {
        createRole("ADMIN", null);
        createRole("DEVELOPER", null);
        createRole("SUPPORT", null);
        LOG.info("Seeded Roles.");
    }

    private void createRole(String name, String desc) {
        Role role = new Role();
        role.name = name;
        role.description = desc;
        roleRepository.persist(role);
    }

    private void seedUsers() {
        // Roles
        Role adminRole = roleRepository.find("name", "ADMIN").firstResult();
        Role devRole = roleRepository.find("name", "DEVELOPER").firstResult();
        Role supportRole = roleRepository.find("name", "SUPPORT").firstResult();

        // 1. ADMIN_DEV
        createUser("admindev@gmail.com", "ADMIN_DEV", adminRole);

        // 2. Developers
        createUser("dev1@gmail.com", "dev1", devRole);
        createUser("dev2@gmail.com", "dev2", devRole);
        createUser("dev3@gmail.com", "dev3", devRole);

        // 3. Support
        createUser("support1@gmail.com", "support1", supportRole);
        createUser("support2@gmail.com", "support2", supportRole);
        createUser("support3@gmail.com", "support3", supportRole);

        LOG.info("Seeded Users.");
    }

    private void createUser(String email, String fullName, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User user = new User();
        user.email = email;
        user.fullName = fullName;
        // Default password for all seeded users: password123
        user.passwordHash = io.github.akbarrizky.util.PasswordUtil.hash("password123");
        user.roles = Set.of(role);
        user.isActive = true;

        userRepository.persist(user);
    }

    private void seedPriorities() {
        createPriority("LOW", 1);
        createPriority("MEDIUM", 2);
        createPriority("HIGH", 3);
        createPriority("CRITICAL", 4);
        LOG.info("Seeded Priorities.");
    }

    private void createPriority(String name, Integer level) {
        TicketPriority p = new TicketPriority();
        p.name = name;
        p.description = null;
        p.level = level;
        priorityRepository.persist(p);
    }

    private void seedStatuses() {
        createStatus("OPEN", "Ticket created");
        createStatus("IN PROGRESS", "Currently being worked on");
        createStatus("Waiting", "Waiting for response");
        createStatus("RESOLVED", "Issue fixed");
        createStatus("CLOSED", "Ticket closed"); // From full_seeder
        LOG.info("Seeded Statuses.");
    }

    private void createStatus(String name, String desc) {
        TicketStatus s = new TicketStatus();
        s.name = name;
        s.description = desc;
        statusRepository.persist(s);
    }

    private void seedCategories() {
        createCategory("Ganti Email", "Permintaan perubahan alamat email akun");
        createCategory("Bug Report", "Laporan bug atau kesalahan pada fitur aplikasi");
        createCategory("Permintaan Fitur Baru", "Usulan penambahan fitur baru pada aplikasi");
        createCategory("Akses Request", "Permintaan akses atau izin ke modul tertentu");
        createCategory("Pertanyaan Umum", "Pertanyaan atau informasi umum seputar aplikasi");
        createCategory("Masalah Registrasi", "Kendala saat proses pendaftaran akun");
        createCategory("Masalah Verifikasi", "Kendala saat proses verifikasi akun");
        createCategory("Laporan Error Aplikasi", "Laporan error teknis pada aplikasi");
        LOG.info("Seeded Categories.");
    }

    private void createCategory(String name, String desc) {
        TicketCategory c = new TicketCategory();
        c.name = name;
        c.description = desc;
        categoryRepository.persist(c);
    }

    private void seedMenus() {
        createMenu("Dashboard", "/dashboard", "mdi-home", 1);
        createMenu("Tickets", "/tickets", "mdi-ticket", 2);
        createMenu("Users", "/users", "mdi-account", 3);
        createMenu("Settings", "/settings", "mdi-cog", 4);
        LOG.info("Seeded Menus.");
    }

    private void createMenu(String label, String path, String icon, Integer order) {
        io.github.akbarrizky.entity.menu.Menu m = new io.github.akbarrizky.entity.menu.Menu();
        m.label = label;
        m.path = path;
        m.icon = icon;
        m.order = order;
        menuRepository.persist(m);
    }
}
