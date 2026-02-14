package com.r2.controller;

import com.r2.dto.LoginRequest;
import com.r2.dto.LoginResponse;
import com.r2.dto.ErrorResponse;
import com.r2.service.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${auth.correct-password}")
    private String correctPassword;

    private final TokenService tokenService;
    
    public AuthController(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    @PostMapping("/login")
    @SecurityRequirements()  // no auth required
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        // Validate email format (handled by @Email annotation)
        // Validate password
        if (!correctPassword.equals(request.getPassword())) {
            logger.error("Wrong email and password");
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
            logger.error("Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token"));
        }
        
        tokenService.invalidateToken(token);
        return ResponseEntity.ok("OK");
    }

}
