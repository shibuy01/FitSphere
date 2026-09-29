package com.fitness.userService.services;

import com.fitness.userService.dto.AuthenticationRequest;
import com.fitness.userService.dto.AuthenticationResponse;
import com.fitness.userService.dto.RegistorRequest;
import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.models.User;
import com.fitness.userService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // =========================
    // REGISTER USER
    // =========================
    public UserResponse registor(RegistorRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setEmail(request.getEmail());

        // Password encrypt hoga
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User saveUser = userRepository.save(user);

        UserResponse response = new UserResponse();

        response.setId(saveUser.getId());
        response.setEmail(saveUser.getEmail());
        response.setFirstName(saveUser.getFirstName());
        response.setLastName(saveUser.getLastName());

        return response;
    }


    // =========================
    // GET USER PROFILE
    // =========================
    public UserResponse getProfile(String userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());

        // ❌ Password response mein nahi bhejna
        // response.setPassword(user.getPassword());

        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedDate(user.getCreatedDate());
        response.setUpdatedDate(user.getUpdatedDate());

        return response;
    }


    // =========================
    // VALIDATE USER
    // =========================
    public Boolean existByUserId(String userId) {

        log.info("Calling userService for {}", userId);

        return userRepository.existsById(userId);
    }


    // =========================
    // AUTHENTICATE USER
    // =========================
    public AuthenticationResponse authenticate(
            AuthenticationRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        // User nahi mila
        if (user == null) {

            return new AuthenticationResponse(
                    false,
                    null,
                    null,
                    null
            );
        }

        // Password check
        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        // Password incorrect
        if (!passwordMatches) {

            return new AuthenticationResponse(
                    false,
                    null,
                    null,
                    null
            );
        }

        // Authentication successful
        return new AuthenticationResponse(
                true,
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}