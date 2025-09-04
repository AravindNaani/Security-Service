package com.ecom.Security_Service.service.impl;

import com.ecom.Security_Service.constants.Constants;
import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;
import com.ecom.Security_Service.dto.RegisterRequest;
import com.ecom.Security_Service.dto.RegisterResponse;
import com.ecom.Security_Service.entity.PasswordResetToken;
import com.ecom.Security_Service.entity.RefreshToken;
import com.ecom.Security_Service.entity.Role;
import com.ecom.Security_Service.entity.User;
import com.ecom.Security_Service.exception.AccountLockedException;
import com.ecom.Security_Service.exception.ResourceAlreadyExistsException;
import com.ecom.Security_Service.exception.ResourceNotFoundException;
import com.ecom.Security_Service.repo.PasswordResetTokenRepository;
import com.ecom.Security_Service.repo.RefreshTokenRepository;
import com.ecom.Security_Service.repo.RoleRepository;
import com.ecom.Security_Service.repo.UserRepository;
import com.ecom.Security_Service.service.AuthService;
import com.ecom.Security_Service.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {

        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResourceAlreadyExistsException("Username already registered");
        } else if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }
        Role role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new ResourceAlreadyExistsException("Role Customer not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Collections.singleton(role))
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        return new RegisterResponse("User registered successfully", user.getUsername(), user.getEmail());
    }

    @Override
    public JwtResponse loginUser(LoginRequest loginRequest, HttpServletRequest request) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        if (user.isAccountLocked()) {
            if (user.getLockTime().plusSeconds(Constants.LOCK_DURATION_SECONDS).isAfter(Instant.now())) {
                throw new AccountLockedException("Account is locked. Try again After sometime");
            } else {
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
                user.setLockTime(null);
                user.setEnabled(true);
                userRepository.save(user);
            }
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() > Constants.MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setLockTime(Instant.now());
                user.setEnabled(false);
                userRepository.save(user);
                throw new AccountLockedException("Account is locked due to multiple failed login attempts. Try again after some time");
            }
            userRepository.save(user);
            throw new ResourceNotFoundException("Invalid password");
        }


        String token = jwtUtil.generateToken(user);
        RefreshToken refreshToken = jwtUtil.generateRefreshToken(user.getId(), request.getRemoteAddr());
        log.info("User logged in successfully: {}", user.getUsername());
        return new JwtResponse(token, refreshToken.getRefreshToken());
    }

    @Override
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)); // Find user by email

        passwordResetTokenRepository.deleteByUserId(user.getId()); // Delete existing tokens for the user if any

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(java.util.UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(Instant.now().plus(15, ChronoUnit.MINUTES)); // Token valid for 15 minutes

        passwordResetTokenRepository.save(token); // Save the new token
        return "http://localhost:8092/ecom/securityservice/api/v1/auth/reset-password?token=" + token.getToken(); // return reset link
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("Password reset token has expired");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user); // Update user's password
        refreshTokenRepository.deleteAllByUserId(user.getId());
        passwordResetTokenRepository.delete(resetToken);
    }

    @Override
    public void logOutAllDevices(String token) {

        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Username: " + username));

        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(user.getId());

        if (tokens.isEmpty()) {
            throw new ResourceNotFoundException("No active sessions found for user with Id: " + user.getId());
        }

        refreshTokenRepository.deleteAll(tokens);
    }
}

