package com.ecom.Security_Service.controller;

import com.ecom.Security_Service.constants.Constants;
import com.ecom.Security_Service.dto.ApiResponse;
import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;
import com.ecom.Security_Service.service.IPasswordService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/security")
public class PasswordController {

    @Autowired
    private IPasswordService passwordService;

    @PostMapping("/hash-password")
    public ResponseEntity<ApiResponse> hashPassword(@NotNull String password){

        String hassedPassword = passwordService.hashPassword(password);
        return ResponseEntity.ok(new ApiResponse(Constants.successful, LocalDateTime.now(),hassedPassword));
    }

    @PostMapping("/validate-credentials")
    public ResponseEntity<ApiResponse> validateCredentials(@RequestBody LoginRequest loginRequest){
        JwtResponse jwtResponse = passwordService.validateCredentials(loginRequest);
        return ResponseEntity.ok(new ApiResponse("SUCCESSFULL", LocalDateTime.now(), jwtResponse));
    }
}
