package com.fitness.userService.dto;

import com.fitness.userService.models.USerRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {

    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private USerRole role = USerRole.USER;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
