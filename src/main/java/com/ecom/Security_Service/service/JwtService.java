package com.ecom.Security_Service.service;

public interface JwtService {

    boolean validateToken(String token);

    String getValidationError(String token);
}
