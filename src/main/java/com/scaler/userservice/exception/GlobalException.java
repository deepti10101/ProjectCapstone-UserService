package com.scaler.userservice.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> exception( ) {
        return new ResponseEntity<>("Internal Server Error ",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
