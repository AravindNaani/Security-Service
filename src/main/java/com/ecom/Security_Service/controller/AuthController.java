package com.ecom.Security_Service.controller;

import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;
import com.ecom.Security_Service.dto.RegisterRequest;
import com.ecom.Security_Service.dto.RegisterResponse;
import com.ecom.Security_Service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest request){
        RegisterResponse registerResponse = authService.registerUser(request);
        return ResponseEntity.created(null).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest loginRequest){
        JwtResponse jwtResponse = authService.loginUser(loginRequest);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
}
