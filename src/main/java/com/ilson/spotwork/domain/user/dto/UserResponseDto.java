package com.ilson.spotwork.domain.user.dto;

import com.ilson.spotwork.domain.user.entity.Role;
import com.ilson.spotwork.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class UserResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private String phone;
    private Role role;
    private BigDecimal avgRating;
    private String profileImage;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .role(user.getRole())
                .avgRating(user.getAvgRating())
                .profileImage(user.getProfileImage())
                .build();
    }
}