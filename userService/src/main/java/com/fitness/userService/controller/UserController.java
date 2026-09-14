package com.fitness.userService.controller;

import com.fitness.userService.dto.RegistorRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/registor")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegistorRequest request){
       return ResponseEntity.ok(userService.registor(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        return ResponseEntity.ok(userService.getProfile(userId));
    }

}
