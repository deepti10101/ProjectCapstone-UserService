package com.scaler.userservice.controllers;

import com.scaler.userservice.dtos.*;
import com.scaler.userservice.models.Token;
import com.scaler.userservice.models.User;
import com.scaler.userservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @Autowired
    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signUp")
    public SignUpResponseDto signup(@RequestBody SignUpRequestDto signUpRequestDto){
        SignUpResponseDto responseDto = new SignUpResponseDto();
        User user=userService.signUp(
                signUpRequestDto.getName(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());
        if(user==null){
            responseDto.setErrorDto(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR,
                            "User signup failed"));
        }
        responseDto.setName(signUpRequestDto.getName());
        responseDto.setEmail(signUpRequestDto.getEmail());
        responseDto.setErrorDto(
                new ErrorDto(HttpStatus.OK,
                        "User signup successful"));
        return responseDto;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto responseDto = new LoginResponseDto();
        Token token = userService.login(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword());
        System.out.println("token "+token);
        if(token == null){
             responseDto.setErrorDto(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error om finding Token"));
             return responseDto;
        }
        responseDto.setToken(token);
        responseDto.setErrorDto(
                new ErrorDto(HttpStatus.OK,
                        "Login signup successful"));
        return responseDto;
    }

    @PostMapping("/validate")
    public UserDto validate(@RequestHeader("Authorization")String token){
       User user=userService.validate(token);
        if(user==null){
            UserDto userDto = new UserDto();
            userDto.setErrorDto(new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error validating Token"));
            return userDto;
        }
        return UserDto.fromUser(user);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void>  logout(@RequestHeader("Authorization") String token){
        userService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
