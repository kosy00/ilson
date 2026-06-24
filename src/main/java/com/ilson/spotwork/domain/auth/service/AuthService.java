package com.ilson.spotwork.domain.auth.service;

import com.ilson.spotwork.common.exception.CustomException;
import com.ilson.spotwork.common.exception.ErrorCode;
import com.ilson.spotwork.domain.auth.dto.LoginRequestDto;
import com.ilson.spotwork.domain.auth.dto.SignupRequestDto;
import com.ilson.spotwork.domain.auth.dto.TokenResponseDto;
import com.ilson.spotwork.domain.user.entity.User;
import com.ilson.spotwork.domain.user.repository.UserRepository;
import com.ilson.spotwork.infra.redis.RefreshTokenRepository;
import com.ilson.spotwork.infra.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //회원가입
    @Transactional
    public void signup(SignupRequestDto request) {
        //중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();

        userRepository.save(user);
        log.info("[Auth] 회원가입 완료");
    }

    // 로그인
    @Transactional
    public TokenResponseDto login(LoginRequestDto request) {
        //유저 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //비번 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        //블랙리스트 체크
        if (user.isLocked()) {
            throw new CustomException(ErrorCode.LOCKED_USER);
        }

        //토큰 발급
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        refreshTokenRepository.save(user.getId(), refreshToken);

        log.info("[Auth] 로그인 완료 userId = {}", user.getId());

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //토큰 재발급
    @Transactional
    public TokenResponseDto reissue(String refreshToken) {
        //토큰 검증
        jwtProvider.validateToken(refreshToken);
        Long userId = jwtProvider.getUserId(refreshToken);

        // 저장된 토큰과 비교 후 즉시 삭제 (원자적 처리로 동시 재발급 방지)
        if (!refreshTokenRepository.compareAndDelete(userId, refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        //유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //새 토큰 발급
        String newAccessToken = jwtProvider.generateAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());

        refreshTokenRepository.save(userId, newRefreshToken);

        log.info("[Auth] 토큰 재발급 완료 userId = {}", userId);
        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

    }

    //로그아웃
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.delete(userId);
        log.info("[Auth] 로그아웃 완료 userId = {}", userId);
    }
}
