package com.ecom.Security_Service.exception;

import com.ecom.Security_Service.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> InvalidCredentialsException(InvalidCredentialsException ex){
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "Bad Credentials",
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN);
    }
}
