package com.ecom.Security_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtValidationRequest {

    private String token;
}
