package com.ecom.Security_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtValidationResponse {

    private boolean valid;
    private String error;
    private String message;
}
