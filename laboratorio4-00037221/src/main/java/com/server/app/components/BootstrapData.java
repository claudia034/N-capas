package com.server.app.components;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.server.app.entities.Permission;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.repositories.PermissionRepository;
import com.server.app.repositories.RoleRepository;
import com.server.app.repositories.UserRepository;

@Component
@Order(2)
public class BootstrapData implements ApplicationListener<ApplicationReadyEvent> {

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "00037221";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapData(RoleRepository roleRepository,
                         PermissionRepository permissionRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Role adminRole = getOrCreateRole("ADMIN");
        Role userRole = getOrCreateRole("USER");

        Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
        addPermissions(adminRole, allPermissions);

        Set<Permission> getPermissions = new HashSet<>();
        for (Permission permission : allPermissions) {
            if ("GET".equalsIgnoreCase(permission.getMethod())) {
                getPermissions.add(permission);
            }
        }
        addPermissions(userRole, getPermissions);

        createDefaultAdmin(adminRole);
    }

    private Role getOrCreateRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setPermissions(new HashSet<>());
            return roleRepository.save(role);
        });
    }

    private void addPermissions(Role role, Set<Permission> permissions) {
        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        role.getPermissions().addAll(permissions);
        roleRepository.save(role);
    }

    private void createDefaultAdmin(Role adminRole) {
        userRepository.findUserByUsername(DEFAULT_ADMIN_USERNAME).ifPresentOrElse(existing -> {
            boolean changed = false;
            if (existing.getRole() == null) {
                existing.setRole(adminRole);
                changed = true;
            }
            if (!passwordEncoder.matches(DEFAULT_ADMIN_PASSWORD, existing.getPassword())) {
                existing.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
                changed = true;
            }
            if (changed) {
                userRepository.save(existing);
            }
        }, () -> {
            User admin = new User();
            admin.setUsername(DEFAULT_ADMIN_USERNAME);
            admin.setName("Admin");
            admin.setSurname("Sistema");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            admin.setRole(adminRole);
            userRepository.save(admin);
        });
    }
}
