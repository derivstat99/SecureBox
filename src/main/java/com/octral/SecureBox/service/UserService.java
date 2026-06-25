package com.octral.SecureBox.service;

import com.octral.SecureBox.dto.AuthResponse;
import com.octral.SecureBox.dto.LoginRequest;
import com.octral.SecureBox.dto.RegisterRequest;
import com.octral.SecureBox.dto.UserResponse;
import com.octral.SecureBox.model.User;
import com.octral.SecureBox.repository.UserRepository;
import com.octral.SecureBox.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User saved = userRepository.save(user);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(saved.getUsername())
                .password(saved.getPassword())
                .authorities("USER")
                .build();

        String token = jwtService.generateToken(userDetails, saved.getId());
        return new AuthResponse(token, UserResponse.from(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();

        String token = jwtService.generateToken(userDetails, user.getId());
        return new AuthResponse(token, UserResponse.from(user));
    }

    public UserResponse getProfile(User user) {
        return UserResponse.from(user);
    }
}
