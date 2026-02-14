package com.r2.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    
    /** Thread-safe: multiple request threads may access tokens concurrently. */
    private final Set<String> validTokens = ConcurrentHashMap.newKeySet();

    public String generateToken() {
        String token = UUID.randomUUID().toString();
        validTokens.add(token);
        return token;
    }

    public boolean isValidToken(String token) {
        return validTokens.contains(token);
    }

    public void invalidateToken(String token) {
        validTokens.remove(token);
    }

    public String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
