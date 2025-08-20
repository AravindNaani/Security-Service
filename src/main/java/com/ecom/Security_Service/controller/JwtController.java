package com.ecom.Security_Service.controller;

import com.ecom.Security_Service.dto.JwtValidationRequest;
import com.ecom.Security_Service.dto.JwtValidationResponse;
import com.ecom.Security_Service.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/security")
public class JwtController {

    @Autowired
    private JwtService jwtService;
    
    @PostMapping("/validate-token")
    public ResponseEntity<JwtValidationResponse> validateJwtToken(@RequestHeader("Authorization") @RequestBody JwtValidationRequest jwtValidationRequest){
        boolean isValid = jwtService.validateToken(jwtValidationRequest.getToken());
        if(isValid){
            return ResponseEntity.ok(new JwtValidationResponse(true,null, "Token is Valid"));
        }else {
            String validationError = jwtService.getValidationError(jwtValidationRequest.getToken());
            return new ResponseEntity<>((new JwtValidationResponse(false, validationError,"Token Validation Failed")), HttpStatus.BAD_REQUEST);
        }
    }
}
