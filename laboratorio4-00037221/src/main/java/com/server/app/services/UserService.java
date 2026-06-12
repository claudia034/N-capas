package com.server.app.services;

import com.server.app.config.JsonWebToken;
import com.server.app.dto.auth.AuthResponse;
import com.server.app.dto.auth.LoginDto;
import com.server.app.dto.auth.SignupDto;
import com.server.app.dto.auth.UpdatePasswordDto;
import com.server.app.dto.auth.UpdateProfileDto;
import com.server.app.dto.user.UserCreateDto;
import com.server.app.dto.user.UserUpdateDto;
import com.server.app.entities.Role;
import com.server.app.entities.User;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.ConfictException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.exceptions.UnauthorizedException;
import com.server.app.repositories.RoleRepository;
import com.server.app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private static final String DEFAULT_SIGNUP_ROLE = "ADMIN";
    private static final String DEFAULT_USER_ROLE = "USER";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JsonWebToken jsonWebToken;

    @Transactional
    public AuthResponse login(LoginDto dto) {
        User user = userRepository.findUserByUsername(dto.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        if (user.isBlocked()) {
            throw new UnauthorizedException("Your account has been blocked");
        }

        return new AuthResponse(jsonWebToken.createToken(user), user);
    }

    @Transactional
    public AuthResponse signUp(SignupDto dto) {
        uniqueUsername(dto.getUsername(), null);
        uniqueEmail(dto.getEmail(), null);

        Role role = findRoleByName(DEFAULT_SIGNUP_ROLE);
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(role);

        User saved = userRepository.save(user);
        return new AuthResponse(jsonWebToken.createToken(saved), saved);
    }

    @Transactional
    public User create(UserCreateDto dto) {
        uniqueUsername(dto.getUsername(), null);
        uniqueEmail(dto.getEmail(), null);

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(resolveRole(dto.getRole(), DEFAULT_USER_ROLE));

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(int page, int size, String search) {
        return userRepository.findAll(PageRequest.of(page, size), search == null ? "" : search);
    }

    @Transactional(readOnly = true)
    public User findById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found " + userId));
    }

    @Transactional
    public User updateUser(int userId, UserUpdateDto dto) {
        User user = findById(userId);

        if (user.isBlocked()) {
            throw new ConfictException("The user: " + user.getUsername() + " is locked");
        }

        applyProfileChanges(user, dto.getUsername(), dto.getName(), dto.getSurname(), dto.getEmail());

        if (dto.getBlocked() != null) {
            user.setBlocked(dto.getBlocked());
        }

        if (dto.getRole() != null) {
            Role role = roleRepository.findById(dto.getRole())
                    .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
            user.setRole(role);
        }

        return userRepository.save(user);
    }

    @Transactional
    public AuthResponse updateProfile(int userId, UpdateProfileDto dto) {
        User user = findById(userId);
        applyProfileChanges(user, dto.getUsername(), dto.getName(), dto.getSurname(), dto.getEmail());
        User saved = userRepository.save(user);
        return new AuthResponse(jsonWebToken.createToken(saved), saved);
    }

    @Transactional
    public User updatePassword(int userId, UpdatePasswordDto dto) {
        User user = findById(userId);

        if (!passwordEncoder.matches(dto.getOldpassword(), user.getPassword())) {
            throw new UnauthorizedException("La contraseña actual es incorrecta");
        }

        if (!dto.getNewpassword().equals(dto.getConfirmpassword())) {
            throw new BadRequestException("La confirmación de contraseña no coincide");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewpassword()));
        return userRepository.save(user);
    }

    private void applyProfileChanges(User user, String username, String name, String surname, String email) {
        if (username != null && !username.isBlank()) {
            uniqueUsername(username, user.getId());
            user.setUsername(username);
        }

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        if (surname != null && !surname.isBlank()) {
            user.setSurname(surname);
        }

        if (email != null && !email.isBlank()) {
            uniqueEmail(email, user.getId());
            user.setEmail(email);
        }
    }

    private Role resolveRole(Long roleId, String defaultRoleName) {
        if (roleId != null) {
            return roleRepository.findById(roleId)
                    .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
        }
        return findRoleByName(defaultRoleName);
    }

    private Role findRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado: " + roleName));
    }

    private void uniqueUsername(String username, Integer id) {
        userRepository.findUserByUsername(username).ifPresent(existing -> {
            if (id == null || existing.getId() != id) {
                throw new ConfictException("El nombre de usuario ya está en uso");
            }
        });
    }

    private void uniqueEmail(String email, Integer id) {
        userRepository.findUserByEmail(email).ifPresent(existing -> {
            if (id == null || existing.getId() != id) {
                throw new ConfictException("El correo electrónico ya está en uso");
            }
        });
    }
}
