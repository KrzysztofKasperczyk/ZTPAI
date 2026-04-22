package com.example.demo;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;
import com.example.demo.service.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(AppUserRepository appUserRepository,
                          BCryptPasswordEncoder passwordEncoder,
                          TokenService tokenService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        AppUser saved = appUserRepository.save(user);

        return new UserResponse(saved.getId(), saved.getUsername(), saved.getPassword());
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        String token = tokenService.generateToken(user.getUsername());

        return new LoginResponse(token);
    }
}