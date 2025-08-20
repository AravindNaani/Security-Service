package com.ecom.Security_Service.service;

import com.ecom.Security_Service.dto.JwtResponse;
import com.ecom.Security_Service.dto.LoginRequest;

public interface IPasswordService {

    String hashPassword(String password);

    JwtResponse validateCredentials(LoginRequest loginRequest);
}
