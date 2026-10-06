package com.fitness.userService.services;

import com.fitness.userService.dto.RegistorRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.models.User;
import com.fitness.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;


    // REGISTER / SYNC KEYCLOAK USER
    public UserResponse registor(RegistorRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            User exitsUser = userRepository.findByEmail(request.getEmail());

            UserResponse response = new UserResponse();
            response.setId(exitsUser.getId());
            response.setEmail(exitsUser.getEmail());
            response.setFirstName(exitsUser.getFirstName());
            response.setLastName(exitsUser.getLastName());
            response.setKeycloakId(exitsUser.getKeycloakId());

            return response;
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setKeycloakId(request.getKeycloakId());

        // ❌ Password Keycloak handle karega
        // user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setKeycloakId(savedUser.getKeycloakId());

        return response;
    }


    // GET USER PROFILE
    public UserResponse getProfile(String userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setKeycloakId(user.getKeycloakId());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());

        return response;
    }


    // VALIDATE KEYCLOAK USER
    public Boolean existByUserId(String userId) {

        log.info("Calling userService for {}", userId);

        return userRepository.existsByKeycloakId(userId);
    }
}