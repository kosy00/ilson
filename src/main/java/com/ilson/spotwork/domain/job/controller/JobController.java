package com.ilson.spotwork.domain.job.controller;

import com.ilson.spotwork.common.response.ApiResponse;
import com.ilson.spotwork.domain.job.dto.JobCreateRequestDto;
import com.ilson.spotwork.domain.job.dto.JobResponseDto;
import com.ilson.spotwork.domain.job.dto.JobUpdateRequestDto;
import com.ilson.spotwork.domain.job.service.JobService;
import com.ilson.spotwork.infra.security.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    // 공고 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<JobResponseDto> register(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid JobCreateRequestDto request) {
        JobResponseDto response = jobService.register(userDetails.getUserId(), request);
        return ApiResponse.success(response);
    }

    // 공고 목록 조회
    @GetMapping
    public ApiResponse<Slice<JobResponseDto>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<JobResponseDto> response = jobService.getList(pageable);
        return ApiResponse.success(response);
    }

    // 공고 상세 조회
    @GetMapping("/{jobId}")
    public ApiResponse<JobResponseDto> getDetail(@PathVariable Long jobId) {
        JobResponseDto response = jobService.getDetail(jobId);
        return ApiResponse.success(response);
    }

    // 공고 수정
    @PatchMapping("/{jobId}")
    public ApiResponse<JobResponseDto> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long jobId,
            @RequestBody @Valid JobUpdateRequestDto request) {
        return ApiResponse.success(jobService.update(userDetails.getUserId(), jobId, request));
    }

    // 공고 삭제
    @DeleteMapping("/{jobId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long jobId) {
        jobService.delete(userDetails.getUserId(), jobId);
        return ApiResponse.success();
    }

    // 내 공고 목록 조회
    @GetMapping("/me")
    public ApiResponse<Slice<JobResponseDto>> getMyJobs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(jobService.getMyJobs(userDetails.getUserId(), pageable));
    }

}
