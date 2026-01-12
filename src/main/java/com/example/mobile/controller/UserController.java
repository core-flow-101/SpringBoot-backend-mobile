package com.example.mobile.controller;

import com.example.mobile.dto.UserLeaderboardDto;
import com.example.mobile.dto.UserProfileDto;
import com.example.mobile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserLeaderboardDto>> leaderboard() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @PostMapping(value = "/profile/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDto> updatePhoto(@RequestParam("photo") MultipartFile file) {
        try {
            byte[] photoBytes = file.getBytes();
            return ResponseEntity.ok(userService.updatePhoto(photoBytes));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке фото", e);
        }
    }
}
