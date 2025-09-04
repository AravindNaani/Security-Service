package com.ecom.Security_Service.service;

import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;
import com.ecom.Security_Service.dto.RegisterRequest;
import com.ecom.Security_Service.dto.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    RegisterResponse registerUser(RegisterRequest request);

    JwtResponse loginUser(LoginRequest loginRequest, HttpServletRequest request);

    String forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    void logOutAllDevices(String token);
}
