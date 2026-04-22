package com.example.demo;

import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TokenService tokenService;
    private final AppUserRepository appUserRepository;

    public AdminController(TokenService tokenService, AppUserRepository appUserRepository) {
        this.tokenService = tokenService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/dashboard")
    public Map<String, String> adminDashboard(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        String token = authorizationHeader.substring(7);

        if (!tokenService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        String username = tokenService.getUsernameFromToken(token);

        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!"ADMIN".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: ADMIN only");
        }

        return Map.of(
                "message", "Welcome admin",
                "username", user.getUsername(),
                "role", user.getRole()
        );
    }
}