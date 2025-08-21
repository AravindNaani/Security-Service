package com.ecom.Security_Service.controller;

import com.ecom.Security_Service.constants.Constants;
import com.ecom.Security_Service.dto.ApiResponse;
import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;
import com.ecom.Security_Service.dto.PasswordRequest;
import com.ecom.Security_Service.service.IPasswordService;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/security")
public class PasswordController {

    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);

    @Autowired
    private IPasswordService passwordService;

    @PostMapping("/hash-password")
    public ResponseEntity<ApiResponse> hashPassword(@RequestHeader("X-Trace-Id") String TraceId,
                                                    @RequestBody @NotNull PasswordRequest password) {
        logger.debug("Received request to hash-password with TraceId: {}", TraceId);
        String hassedPassword = passwordService.hashPassword(password.getPassword());
        return ResponseEntity.ok(new ApiResponse(Constants.successful, LocalDateTime.now(), hassedPassword));
    }

    @PostMapping("/validate-credentials")
    public ResponseEntity<ApiResponse> validateCredentials(@RequestHeader("X-Trace-Id") String TraceId,
                                                            @RequestBody LoginRequest loginRequest) {
        logger.debug("Received request to validate-credentials with TraceId: {}", TraceId);
        JwtResponse jwtResponse = passwordService.validateCredentials(loginRequest);
        return ResponseEntity.ok(new ApiResponse("SUCCESSFULL", LocalDateTime.now(), jwtResponse));
    }
}
