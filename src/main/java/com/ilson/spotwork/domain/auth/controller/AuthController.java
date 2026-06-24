package com.ilson.spotwork.domain.auth.controller;

import com.ilson.spotwork.common.exception.CustomException;
import com.ilson.spotwork.common.exception.ErrorCode;
import com.ilson.spotwork.common.response.ApiResponse;
import com.ilson.spotwork.domain.auth.dto.LoginRequestDto;
import com.ilson.spotwork.domain.auth.dto.SignupRequestDto;
import com.ilson.spotwork.domain.auth.dto.TokenResponseDto;
import com.ilson.spotwork.domain.auth.service.AuthService;
import com.ilson.spotwork.infra.security.jwt.CustomUserDetails;
import com.ilson.spotwork.infra.security.jwt.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    //회원가입
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> signup(@RequestBody @Valid SignupRequestDto request) {
        authService.signup(request);
        return ApiResponse.success();
    }

    //로그인
    @PostMapping("/login")
    public ApiResponse<TokenResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        return ApiResponse.success(authService.login(request));
    }

    //토큰 재발급
    @PostMapping("/reissue")
    public ApiResponse<TokenResponseDto> reissue(
            @RequestHeader("Authorization") String bearerToken) {
        if (!bearerToken.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        String refreshToken = bearerToken.substring(7);
        return ApiResponse.success(authService.reissue(refreshToken));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getUserId());
        return ApiResponse.success();
    }
}
