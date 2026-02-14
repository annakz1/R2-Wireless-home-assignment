package com.r2.controller;

import com.r2.dto.TryLuckResponse;
import com.r2.dto.ErrorResponse;
import com.r2.service.TokenService;
import com.r2.service.WinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LuckController {
    
    private static final Logger logger = LoggerFactory.getLogger(LuckController.class);
    
    private final TokenService tokenService;
    private final WinService winService;
    
    public LuckController(TokenService tokenService, WinService winService) {
        this.tokenService = tokenService;
        this.winService = winService;
    }
    
    @PostMapping("/try_luck")
    public ResponseEntity<?> tryLuck(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        String token = tokenService.extractToken(authHeader);
        
        if (token == null || !tokenService.isValidToken(token)) {
            logger.error("Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token"));
        }
        
        boolean win = winService.calculateWin();
        return ResponseEntity.ok(new TryLuckResponse(win));
    }
    
}
