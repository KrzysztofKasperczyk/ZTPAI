package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, username);
        return token;
    }

    public boolean isValid(String token) {
        return tokens.containsKey(token);
    }

    public String getUsernameFromToken(String token) {
        return tokens.get(token);
    }
}