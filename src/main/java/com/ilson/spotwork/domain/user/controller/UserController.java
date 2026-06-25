package com.ilson.spotwork.domain.user.controller;

import com.ilson.spotwork.common.response.ApiResponse;
import com.ilson.spotwork.domain.user.dto.UserResponseDto;
import com.ilson.spotwork.domain.user.dto.UserUpdateRequestDto;
import com.ilson.spotwork.domain.user.service.UserService;
import com.ilson.spotwork.infra.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ApiResponse<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(userService.getMyInfo(userDetails.getUserId()));
    }

    // 내 정보 수정
    @PatchMapping("/me")
    public ApiResponse<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserUpdateRequestDto request) {
        return ApiResponse.success(
                userService.updateMyInfo(userDetails.getUserId(), request));
    }
}