package com.fitness.userService.services;

import com.fitness.userService.dto.RegistorRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.models.User;
import com.fitness.userService.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service

public class UserService {

    private final UserRepository userRepository;

    public UserResponse registor(RegistorRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User saveUser = userRepository.save(user);
        UserResponse response = new UserResponse();

        response.setId(saveUser.getId());
        response.setEmail(saveUser.getEmail());
        response.setPassword(saveUser.getPassword());
        response.setFirstName(saveUser.getFirstName());
        response.setLastName(saveUser.getLastName());

        return response;
    }

    public UserResponse getProfile(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());

        return response;
    }
}
