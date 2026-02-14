package com.r2.controller;

import com.r2.dto.LoginRequest;
import com.r2.dto.LoginResponse;
import com.r2.dto.ErrorResponse;
import com.r2.service.TokenService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    
    private static final String CORRECT_PASSWORD = "r2isthebest";
    
    private final TokenService tokenService;
    
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // Validate email format (handled by @Email annotation)
        // Validate password
        if (!CORRECT_PASSWORD.equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Wrong email and password"));
        }
        
        String token = tokenService.generateToken();
        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        String token = tokenService.extractToken(authHeader);
        
        if (token == null || !tokenService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token"));
        }
        
        tokenService.invalidateToken(token);
        return ResponseEntity.ok("OK");
    }

}
