package com.scaler.userservice.dtos;

import com.scaler.userservice.models.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class SignUpResponseDto {
    private String name;
    private String email;
    private ErrorDto errorDto;

}
