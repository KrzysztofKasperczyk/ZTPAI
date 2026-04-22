package com.example.demo;

import com.example.demo.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProtectedController {

    private final TokenService tokenService;

    public ProtectedController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/profile")
    public Map<String, String> profile(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
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

        return Map.of(
                "message", "Access granted",
                "username", username
        );
    }
}