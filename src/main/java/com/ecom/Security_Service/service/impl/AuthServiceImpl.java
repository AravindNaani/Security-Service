package com.ecom.Security_Service.service.impl;

import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;
import com.ecom.Security_Service.dto.RegisterRequest;
import com.ecom.Security_Service.dto.RegisterResponse;
import com.ecom.Security_Service.entity.Role;
import com.ecom.Security_Service.entity.User;
import com.ecom.Security_Service.exception.ResourceAlreadyExistsException;
import com.ecom.Security_Service.exception.ResourceNotFoundException;
import com.ecom.Security_Service.repo.RoleRepository;
import com.ecom.Security_Service.repo.UserRepository;
import com.ecom.Security_Service.service.AuthService;
import com.ecom.Security_Service.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
    public JwtResponse loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new ResourceNotFoundException("Invalid password");
        }

        String role = user.getRoles().stream().findFirst()
                .map(r -> r.getName()).orElse("ROLE_CUSTOMER");

        String token = jwtUtil.generateToken(user.getUsername(), role);
        log.info("User logged in successfully: {}", user.getUsername());
        return new JwtResponse(token, role);
    }
}

