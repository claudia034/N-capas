package com.server.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.app.dto.auth.AuthResponse;
import com.server.app.dto.auth.LoginDto;
import com.server.app.dto.auth.SignupDto;
import com.server.app.dto.auth.UpdatePasswordDto;
import com.server.app.dto.auth.UpdateProfileDto;
import com.server.app.entities.User;
import com.server.app.exceptions.UnauthorizedException;
import com.server.app.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupDto dto) {
        return ResponseEntity.ok(userService.signUp(dto));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> profile(Authentication authentication) {
        return ResponseEntity.ok(currentUser(authentication));
    }

    @PutMapping("/update/profile")
    public ResponseEntity<AuthResponse> updateProfile(Authentication authentication,
                                                      @Valid @RequestBody UpdateProfileDto dto) {
        return ResponseEntity.ok(userService.updateProfile(currentUser(authentication).getId(), dto));
    }

    @PutMapping("/update/password")
    public ResponseEntity<User> updatePassword(Authentication authentication,
                                               @Valid @RequestBody UpdatePasswordDto dto) {
        return ResponseEntity.ok(userService.updatePassword(currentUser(authentication).getId(), dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        return user;
    }
}
