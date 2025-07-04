package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class ProfileImageResponse {
    private final String profileImageUrl;

    public ProfileImageResponse(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
