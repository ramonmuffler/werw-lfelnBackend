package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.User;
import com.ausganslage.ausgangslageBackend.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public record AuthRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Size(min = 4, max = 100) String password
    ) {}

    public record UserResponse(
            Long id,
            String username,
            int villagerWins,
            int villagerLosses,
            int werewolfWins,
            int werewolfLosses
    ) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody AuthRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new IllegalArgumentException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody AuthRequest request) {
        User user = userRepository
                .findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return toResponse(user);
    }

    @GetMapping("/profile/{userId}")
    public UserResponse getProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getVillagerWins(),
                user.getVillagerLosses(),
                user.getWerewolfWins(),
                user.getWerewolfLosses()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException ex) {
        return Map.of("error", ex.getMessage());
    }
}


