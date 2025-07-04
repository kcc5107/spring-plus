package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.CustomUserDetails;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.ProfileImageResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(customUserDetails.getId(), userChangePasswordRequest);
    }

    @PostMapping("/users/{userId}/profileImage")
    public ResponseEntity<ProfileImageResponse> uploadImage(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @RequestParam("imageUrl") MultipartFile file) throws IOException {
        ProfileImageResponse result = userService.uploadProfileImage(file, customUserDetails.getId());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{userId}/profileImage")
    public ResponseEntity<Void> deleteProfileImage(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        userService.deleteProfileImage(customUserDetails.getId());
        return ResponseEntity.ok().build();
    }
}
