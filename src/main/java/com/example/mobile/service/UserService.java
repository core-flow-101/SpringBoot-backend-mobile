package com.example.mobile.service;

import com.example.mobile.config.JwtUtil;
import com.example.mobile.dto.AuthResponse;
import com.example.mobile.dto.LoginRequest;
import com.example.mobile.dto.RegisterRequest;
import com.example.mobile.dto.UserLeaderboardDto;
import com.example.mobile.dto.UserProfileDto;
import com.example.mobile.entity.User;
import com.example.mobile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        user = userRepository.save(user);
        

        String token = jwtUtil.generateToken(user.getUsername());
        
        return new AuthResponse(token, "Bearer", user.getUsername(), user.getEmail());
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        
        return new AuthResponse(token, "Bearer", user.getUsername(), user.getEmail());
    }

    public List<UserLeaderboardDto> getLeaderboard() {
        return userRepository.findAllByOrderByPointsDesc()
                .stream()
                .map(u -> UserLeaderboardDto.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .points(u.getPoints())
                        .build())
                .collect(Collectors.toList());
    }

    public UserProfileDto getProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .points(user.getPoints())
                .photo(user.getPhoto())
                .build();
    }

    @Transactional
    public UserProfileDto updatePhoto(byte[] photo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setPhoto(photo);
        user = userRepository.save(user);

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .points(user.getPoints())
                .photo(user.getPhoto())
                .build();
    }
}
