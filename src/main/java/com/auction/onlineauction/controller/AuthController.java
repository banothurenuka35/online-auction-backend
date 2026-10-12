
package com.auction.onlineauction.controller;

import com.auction.onlineauction.entity.User;
import com.auction.onlineauction.repository.UserRepository;
import com.auction.onlineauction.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        if (request.getName() == null ||
            request.getName().isBlank() ||
            request.getEmail() == null ||
            request.getEmail().isBlank() ||
            request.getPassword() == null ||
            request.getPassword().length() < 8) {

            return ResponseEntity.badRequest().body(
                Map.of("error",
                    "Enter your name, email, and a password of at least 8 characters.")
            );
        }

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("error", "An account with this email already exists.")
            );
        }

        User user = new User(
            request.getName().trim(),
            email,
            passwordEncoder.encode(request.getPassword()),
            "BUYER"
        );

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of("message", "Registration successful. Please log in.")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        if (request.getEmail() == null ||
            request.getPassword() == null) {
            return ResponseEntity.status(401).body(
                Map.of("error", "Invalid email or password.")
            );
        }

        User user = userRepository.findByEmail(
            request.getEmail().trim().toLowerCase()
        ).orElse(null);

        if (user == null) {
            return ResponseEntity.status(401).body(
                Map.of("error", "Invalid email or password.")
            );
        }

        String storedPassword = user.getPassword();
        boolean validPassword = false;

        // Supports existing plain-text accounts during migration.
        if (storedPassword != null &&
            storedPassword.startsWith("$2") &&
            passwordEncoder.matches(request.getPassword(), storedPassword)) {
            validPassword = true;
        } else if (storedPassword != null &&
                   storedPassword.equals(request.getPassword())) {
            validPassword = true;

            // Upgrade an existing plain-text password after successful login.
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
        }

        if (!validPassword) {
            return ResponseEntity.status(401).body(
                Map.of("error", "Invalid email or password.")
            );
        }

        String token = jwtService.generateToken(
            user.getEmail(),
            user.getRole()
        );

        return ResponseEntity.ok(token);
    }

    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
