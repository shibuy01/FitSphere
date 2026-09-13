package com.fitness.userService.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class RegistorRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
