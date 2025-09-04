package com.ecom.Security_Service.controller;

import com.ecom.Security_Service.dto.*;
import com.ecom.Security_Service.entity.RefreshToken;
import com.ecom.Security_Service.entity.Role;
import com.ecom.Security_Service.entity.User;
import com.ecom.Security_Service.exception.ResourceNotFoundException;
import com.ecom.Security_Service.exception.TokenRefreshException;
import com.ecom.Security_Service.repo.UserRepository;
import com.ecom.Security_Service.service.AuthService;
import com.ecom.Security_Service.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody RegisterRequest request) {
        RegisterResponse registerResponse = authService.registerUser(request);
        return ResponseEntity.created(null).body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        JwtResponse jwtResponse = authService.loginUser(loginRequest, request);
        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        return jwtUtil.findByToken(request.getRefreshToken())
                .map(rt -> {
                    RefreshToken newRefresfToken = jwtUtil.verifyExpirationAndRotate(rt);
                    String username = newRefresfToken.getUser().getUsername();
                    Set<Role> roles = newRefresfToken.getUser().getRoles();
                    String token = jwtUtil.generateToken(newRefresfToken.getUser());
                    return ResponseEntity.ok(new JwtResponse(token, newRefresfToken.getRefreshToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(), "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        String response = jwtUtil.deleteByToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout-all-devices")
    public ResponseEntity<?> logoutAllDevices(HttpServletRequest httpRequest){

        String authorization = httpRequest.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")){
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authorization.substring(7);
        authService.logOutAllDevices(token);

        return ResponseEntity.ok("Logged out from all devices successfully");
    }

    @PostMapping("/forget-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {

        String resetLink = authService.forgotPassword(request.getEmail());

        // call notification service to send the reset link to user's email

        return ResponseEntity.ok("Password reset link sent to your email");

    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(token, request.getNewPassword());

        return ResponseEntity.ok("Password reset successfully, Login with new password");
    }
}
