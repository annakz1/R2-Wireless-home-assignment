package com.r2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.r2.repo.UserRepository;
import com.r2.dto.*;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.r2.model.User;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${auth.correct-password}")
    private String correctPassword;

    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        String rawPassword = request.getPassword();
        boolean valid = false;

        if (!user.isPresent()) {
            // ✅ User is not registered- accept the fixed password 
            valid = correctPassword.equals(rawPassword);
        } else {
            // ✅ User is registered- accept either the fixed password or the user's stored hash
            valid = correctPassword.equals(rawPassword) ||
                    passwordEncoder.matches(rawPassword, user.get().getPasswordHash());
        }

        if (!valid) {
            throw new RuntimeException("Wrong email or password");
        }

        // Generate token (no email used here, can add later if needed)
        String token = tokenService.generateToken();
        return new LoginResponse(token);
    }

    @Transactional
    public void logout(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null || !tokenService.isValidToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        tokenService.invalidateToken(token);
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return null;
        return authHeader.substring(7);
    }

    @Transactional
    public User register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        String hashed = passwordEncoder.encode(rawPassword);
        User user = new User(email, hashed);
        return userRepository.save(user);
    }
}
