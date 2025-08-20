package com.ecom.Security_Service.service.impl;

import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;
import com.ecom.Security_Service.exception.InvalidCredentialsException;
import com.ecom.Security_Service.service.IPasswordService;
import com.ecom.Security_Service.utils.Jwtutils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceImpl implements IPasswordService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Jwtutils jwtutils;

    @Override
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public JwtResponse validateCredentials(LoginRequest loginRequest) {

        if(!validatePassword(loginRequest.getPassword(),loginRequest.getHashedPassword())){
            throw new InvalidCredentialsException("Bad credentials");
        }
        String token = jwtutils.generateToken(loginRequest);
        JwtResponse jwtResponse = new JwtResponse(token);
        return jwtResponse;
    }

    private boolean validatePassword(String rawPassword, String hashedPassword){
       return passwordEncoder.matches(rawPassword,hashedPassword);
    }
}
