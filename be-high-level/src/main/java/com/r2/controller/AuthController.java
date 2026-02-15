
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
import com.r2.service.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;

    @Value("${auth.correct-password}")
    private String correctPassword;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @SecurityRequirements()
    public ResponseEntity<?> register(@Valid @RequestBody LoginRequest request) {
        userService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @SecurityRequirements() // empty list- means no auth required
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        try {
            userService.logout(authHeader);
            return ResponseEntity.ok("OK");
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

}
