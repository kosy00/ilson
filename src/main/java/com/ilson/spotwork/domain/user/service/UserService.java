package com.ilson.spotwork.domain.user.service;

import com.ilson.spotwork.common.exception.CustomException;
import com.ilson.spotwork.common.exception.ErrorCode;
import com.ilson.spotwork.domain.user.dto.UserResponseDto;
import com.ilson.spotwork.domain.user.dto.UserUpdateRequestDto;
import com.ilson.spotwork.domain.user.entity.User;
import com.ilson.spotwork.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 내 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.from(user);
    }

    // 내 정보 수정
    @Transactional
    public UserResponseDto updateMyInfo(Long userId, UserUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateInfo(request.getNickname(), request.getPhone());
        log.info("[User] 정보 수정 완료 userId = {}", userId);

        return UserResponseDto.from(user);
    }
}
